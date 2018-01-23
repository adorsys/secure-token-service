package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

public class CachedKeyStoreRepository implements KeyStoreRepository {

    private StsKeyStore cachedKeyStore;
    private Boolean keyStoreExists;

    private final KeyStoreRepository decoratedRepository;

    public CachedKeyStoreRepository(KeyStoreRepository decoratedRepository) {
        this.decoratedRepository = decoratedRepository;
    }

    @Override
    public StsKeyStore load() {
        if(cachedKeyStore == null) {
            cachedKeyStore = decoratedRepository.load();
        }

        return cachedKeyStore;
    }

    @Override
    public boolean exists() {
        if(keyStoreExists == null) {
            keyStoreExists = decoratedRepository.exists();
        }

        return keyStoreExists;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        cachedKeyStore = keyStore;
        decoratedRepository.save(keyStore);
    }
}
