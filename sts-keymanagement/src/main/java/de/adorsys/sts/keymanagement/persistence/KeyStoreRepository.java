package de.adorsys.sts.keymanagement.persistence;

import de.adorsys.sts.keymanagement.model.StsKeyStore;

public interface KeyStoreRepository {

    StsKeyStore load();
    boolean exists();
    void save(StsKeyStore keyStore);
}
