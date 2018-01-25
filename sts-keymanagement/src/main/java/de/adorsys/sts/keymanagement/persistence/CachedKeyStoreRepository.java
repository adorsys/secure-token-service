package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

public class CachedKeyStoreRepository implements KeyStoreRepository {

    private final KeyStoreRepository decoratedRepository;

    private StsKeyStore cachedKeyStore;

    public CachedKeyStoreRepository(KeyStoreRepository decoratedRepository) {
        this.decoratedRepository = decoratedRepository;
    }

    @Override
    public StsKeyStore load() {
        if(cachedKeyStore == null) {
            if(decoratedRepository.exists()) {
                cachedKeyStore = decoratedRepository.load();
            }
        }

        return cachedKeyStore;
    }

    @Override
    public boolean exists() {
        return decoratedRepository.exists();
    }

    @Override
    public void save(StsKeyStore keyStore) {
        cachedKeyStore = keyStore;
        decoratedRepository.save(keyStore);
    }
}
