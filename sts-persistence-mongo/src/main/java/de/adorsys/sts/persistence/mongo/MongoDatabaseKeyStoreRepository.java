package de.adorsys.sts.persistence.mongo;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import de.adorsys.sts.persistence.mongo.mapper.KeyStoreEntityMapper;
import de.adorsys.sts.persistence.mongo.repository.MongoKeyStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class MongoDatabaseKeyStoreRepository implements KeyStoreRepository {

    private final MongoKeyStoreRepository keyStoreRepository;
    private final KeyStoreEntityMapper keyStoreEntityMapper;

    private final String keyStoreName;

    private StsKeyStore keyStore;

    @Autowired
    public MongoDatabaseKeyStoreRepository(
            MongoKeyStoreRepository keyStoreRepository,
            KeyStoreEntityMapper keyStoreEntityMapper,
            KeyManagementProperties keyManagementProperties
    ) {
        this.keyStoreRepository = keyStoreRepository;
        this.keyStoreEntityMapper = keyStoreEntityMapper;
        this.keyStoreName = keyManagementProperties.getKeystore().getName();
    }

    @Override
    public StsKeyStore load() {
        if (keyStore != null) {
            keyStoreRepository.findByNameAndLastChangeDateGreaterThan(keyStoreName, keyStore.getLastChangeDate())
                    .ifPresent(keyStoreEntity -> {
                        keyStore = keyStoreEntityMapper.mapFromEntity(keyStoreEntity);
                    });
        } else {
            KeyStoreEntity persistentKeyStore = keyStoreRepository.findByName(keyStoreName);
            keyStore = keyStoreEntityMapper.mapFromEntity(persistentKeyStore);
        }
        return keyStore;
    }

    @Override
    public boolean exists() {
        return keyStoreRepository.countByName(keyStoreName) > 0;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        keyStore.setLastChangeDate(LocalDateTime.now());

        KeyStoreEntity keyStoreEntity = keyStoreRepository.findByName(keyStoreName);

        if (keyStoreEntity == null) {
            keyStoreEntity = keyStoreEntityMapper.mapToEntity(keyStore);
        } else {
            keyStoreEntityMapper.mapIntoEntity(keyStore, keyStoreEntity);
        }
        keyStoreRepository.save(keyStoreEntity);

        this.keyStore = keyStore;
    }
}
