package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

import java.time.ZonedDateTime;

public class InMemoryKeyStoreRepository implements KeyStoreRepository {

    private StsKeyStore keyStore;

    @Override
    public StsKeyStore load() {
        return keyStore;
    }

    @Override
    public boolean exists() {
        return keyStore != null;
    }

    @Override
    public void save(StsKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    @Override
    public ZonedDateTime lastUpdate() {
        if(keyStore != null) {
            return keyStore.getLastUpdate();
        }

        throw new RuntimeException("No keystore exiting");
    }
}
