package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@Slf4j
public class CachedKeyStoreRepository implements KeyStoreRepository {

    private final KeyStoreRepository keyStoreRepository;
    private StsKeyStore cachedKeyStore;

    public CachedKeyStoreRepository(KeyStoreRepository keyStoreRepository) {
        this.keyStoreRepository = keyStoreRepository;
    }

    @Override
    public StsKeyStore load() {
        log.debug("Calling load(). Cached key store last update: {}", cachedKeyStore != null ? cachedKeyStore.getLastUpdate() : null);

        if (cachedKeyStore == null) {
            log.debug("Cache is null, loading from repository");
            cachedKeyStore = keyStoreRepository.load();
        } else {
            ZonedDateTime lastUpdate = keyStoreRepository.lastUpdate();
            ZonedDateTime cachedLastUpdate = cachedKeyStore.getLastUpdate();

            if (lastUpdate.isAfter(cachedLastUpdate)) {
                log.debug("Repository was updated more recently than cache. Refreshing cache.");
                cachedKeyStore = keyStoreRepository.load();
            }
        }

        log.debug("Returning cached key store with last update: {}", cachedKeyStore != null ? cachedKeyStore.getLastUpdate() : null);
        return cachedKeyStore;
    }

    @Override
    public boolean exists() {
        boolean exists = cachedKeyStore != null || keyStoreRepository.exists();

        log.debug("Checking if KeyStore exists. Result: {}", exists);

        return exists;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        log.debug("Saving keyStore to repository...");
        keyStoreRepository.save(keyStore);

        log.debug("Updating cache with newly saved keyStore");
        cachedKeyStore = keyStore;
    }

    @Override
    public ZonedDateTime lastUpdate() {
        ZonedDateTime lastUpdate = keyStoreRepository.lastUpdate();

        log.debug("LastUpdate: {}", lastUpdate);

        return lastUpdate;
    }
}
