package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

public class InMemoryKeyStoreRepository implements KeyStoreRepository {

    private StsKeyStore keyStore;

    @Override
    public StsKeyStore load() {
        return keyStore;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        this.keyStore = keyStore;
    }
}
