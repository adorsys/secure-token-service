package de.adorsys.sts.keymanagement.persistence;

import java.security.KeyStore;

public interface KeyStoreRepository {

    KeyStore load();
    boolean exists();
    void save(KeyStore keyStore);
}
