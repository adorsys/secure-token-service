package de.adorsys.sts.keymanagement.persistence;

import java.security.KeyStore;

public class InMemoryKeyStoreRepository implements KeyStoreRepository {

    private KeyStore keyStore;

    @Override
    public KeyStore load() {
        return keyStore;
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public void save(KeyStore keyStore) {
        this.keyStore = keyStore;
    }
}
