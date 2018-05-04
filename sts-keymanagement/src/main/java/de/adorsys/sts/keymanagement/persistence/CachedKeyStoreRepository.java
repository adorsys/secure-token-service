package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

import java.time.ZonedDateTime;

public class CachedKeyStoreRepository implements KeyStoreRepository {

    private final KeyStoreRepository keyStoreRepository;
    private StsKeyStore cachedKeyStore;

    public CachedKeyStoreRepository(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
    }

    @Override
    public StsKeyStore load() {
        if(cachedKeyStore == null) {
            cachedKeyStore = keyStoreRepository.load();
        } else {
            ZonedDateTime lastUpdate = keyStoreRepository.lastUpdate();
            ZonedDateTime cachedLastUpdate = cachedKeyStore.getLastUpdate();

            if(lastUpdate.isAfter(cachedLastUpdate)) {
                cachedKeyStore = keyStoreRepository.load();
            }
        }

        return cachedKeyStore;
    }

    @Override
    public boolean exists() {
        return cachedKeyStore != null || keyStoreRepository.exists();
    }

    @Override
    public void save(StsKeyStore keyStore) {
        keyStoreRepository.save(keyStore);
        cachedKeyStore = keyStore;
    }

    @Override
    public ZonedDateTime lastUpdate() {
        return keyStoreRepository.lastUpdate();
    }
}
