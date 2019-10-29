package de.adorsys.sts.persistence.jpa;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyEntryAttributes;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyStore;
import de.adorsys.sts.persistence.jpa.mapping.KeyStoreEntityMapper;
import de.adorsys.sts.persistence.jpa.repository.JpaKeyEntryAttributesRepository;
import de.adorsys.sts.persistence.jpa.repository.JpaKeyStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DatabaseKeyStoreRepository implements KeyStoreRepository {

    private final JpaKeyStoreRepository keyStoreRepository;
    private final JpaKeyEntryAttributesRepository keyEntryRepository;
    private final KeyStoreEntityMapper keyStoreEntityMapper;
    private final String keyStoreName;

    @Autowired
    public DatabaseKeyStoreRepository(
            JpaKeyStoreRepository keyStoreRepository,
            JpaKeyEntryAttributesRepository keyEntryRepository,
            KeyStoreEntityMapper keyStoreEntityMapper,
            KeyManagementProperties keyManagementProperties
    ) {
        this.keyStoreRepository = keyStoreRepository;
        this.keyEntryRepository = keyEntryRepository;

        this.keyStoreEntityMapper = keyStoreEntityMapper;
        this.keyStoreName = keyManagementProperties.getKeystore().getName();
    }

    @Override
    public StsKeyStore load() {
        JpaKeyStore persistentKeyStore = keyStoreRepository.findByName(keyStoreName);
        List<JpaKeyEntryAttributes> persistentKeyEntries = keyEntryRepository.findAllByKeyStoreId(persistentKeyStore.getId());

        return keyStoreEntityMapper.mapFromEntity(persistentKeyStore, persistentKeyEntries);
    }

    @Override
    public boolean exists() {
        return keyStoreRepository.countByName(keyStoreName) > 0;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        JpaKeyStore foundKeyStore = keyStoreRepository.findByName(keyStoreName);
        Map<String, StsKeyEntry> stsKeyEntries = keyStore.getEntries();

        if (foundKeyStore == null) {
            foundKeyStore = keyStoreEntityMapper.mapToEntity(keyStore);
            JpaKeyStore savedKeyStore = keyStoreRepository.save(foundKeyStore);

            addKeyEntries(savedKeyStore, stsKeyEntries);
        } else {
            keyStoreEntityMapper.mapIntoEntity(keyStore, foundKeyStore);

            Long keyStoreId = foundKeyStore.getId();
            List<JpaKeyEntryAttributes> keyEntries = keyEntryRepository.findAllByKeyStoreId(keyStoreId);

            for (JpaKeyEntryAttributes keyEntry : keyEntries) {
                if (!stsKeyEntries.containsKey(keyEntry.getAlias())) {
                    keyEntryRepository.deleteById(keyEntry.getId());
                }
            }

            addOrUpdateKeyEntries(foundKeyStore, stsKeyEntries);
            keyStoreRepository.save(foundKeyStore);
        }
    }

    @Override
    public ZonedDateTime lastUpdate() {
        return keyStoreRepository.getLastUpdate(keyStoreName);
    }

    private void addOrUpdateKeyEntries(JpaKeyStore savedKeyStore, Map<String, StsKeyEntry> stsKeyEntries) {
        for (Map.Entry<String, StsKeyEntry> stsKeyEntryMapEntry : stsKeyEntries.entrySet()) {
            JpaKeyEntryAttributes keyEntryAttributes;
            StsKeyEntry stsKeyEntry = stsKeyEntryMapEntry.getValue();

            JpaKeyEntryAttributes foundKeyEntry = keyEntryRepository.findByAlias(stsKeyEntry.getAlias());
            if(foundKeyEntry == null) {
                keyEntryAttributes = new JpaKeyEntryAttributes();
                keyEntryAttributes.setKeyStoreId(savedKeyStore.getId());
            } else {
                keyEntryAttributes = foundKeyEntry;
            }

            keyStoreEntityMapper.mapIntoEntity(stsKeyEntry, keyEntryAttributes);
            keyEntryRepository.save(keyEntryAttributes);
        }
    }

    private void addKeyEntries(JpaKeyStore savedKeyStore, Map<String, StsKeyEntry> stsKeyEntries) {
        for (Map.Entry<String, StsKeyEntry> stsKeyEntryMapEntry : stsKeyEntries.entrySet()) {
            StsKeyEntry stsKeyEntry = stsKeyEntryMapEntry.getValue();

            JpaKeyEntryAttributes keyEntryAttributes = new JpaKeyEntryAttributes();
            keyStoreEntityMapper.mapIntoEntity(stsKeyEntry, keyEntryAttributes);

            keyEntryAttributes.setKeyStoreId(savedKeyStore.getId());

            keyEntryRepository.save(keyEntryAttributes);
        }
    }
}
