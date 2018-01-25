package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;

public class KeyStoreInitializer {

    private final KeyStoreRepository repository;
    private final KeyStoreGenerator generator;

    public KeyStoreInitializer(
            KeyStoreRepository repository,
            KeyStoreGenerator generator
    ) {
        this.repository = repository;
        this.generator = generator;
    }

    public void initialize() {
        if (!repository.exists()) {
            StsKeyStore keyStore = generator.generate();
            repository.save(keyStore);
        }
    }
}
