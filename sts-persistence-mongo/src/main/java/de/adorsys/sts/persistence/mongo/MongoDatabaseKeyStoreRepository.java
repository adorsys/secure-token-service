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

import java.time.ZonedDateTime;
import java.util.List;

@Service
@Transactional
public class MongoDatabaseKeyStoreRepository implements KeyStoreRepository {

    private final MongoKeyStoreRepository keyStoreRepository;
    private final KeyStoreEntityMapper keyStoreEntityMapper;

    private final String keyStoreName;

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
        KeyStoreEntity persistentKeyStore = keyStoreRepository.findByName(keyStoreName);
        return keyStoreEntityMapper.mapFromEntity(persistentKeyStore);
    }

    @Override
    public boolean exists() {
        return keyStoreRepository.countByName(keyStoreName) > 0;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        KeyStoreEntity foundKeyStore = keyStoreRepository.findByName(keyStoreName);

        if(foundKeyStore == null) {
            foundKeyStore = keyStoreEntityMapper.mapToEntity(keyStore);
            keyStoreRepository.save(foundKeyStore);
        } else {
            keyStoreEntityMapper.mapIntoEntity(keyStore, foundKeyStore);
            keyStoreRepository.save(foundKeyStore);
        }
    }

    @Override
    public ZonedDateTime lastUpdate() {
        List<KeyStoreEntity> foundKeyStoresWithLastUpdate = keyStoreRepository.findLastUpdate(keyStoreName);

        if(foundKeyStoresWithLastUpdate.size() > 0) {
            KeyStoreEntity keyStoreWithLastUpdate = foundKeyStoresWithLastUpdate.get(0);

            return keyStoreEntityMapper.mapLastUpdate(keyStoreWithLastUpdate);
        }

        throw new RuntimeException("No keystore found");
    }
}
