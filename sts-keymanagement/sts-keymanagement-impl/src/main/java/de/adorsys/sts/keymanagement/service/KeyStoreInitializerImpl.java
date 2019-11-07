package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;

public class KeyStoreInitializerImpl implements KeyStoreInitializer {

    private final KeyStoreRepository repository;
    private final KeyStoreGenerator generator;

    public KeyStoreInitializerImpl(
            KeyStoreRepository repository,
            KeyStoreGenerator generator
    ) {
        this.repository = repository;
        this.generator = generator;
    }

    @Override
    public boolean initialize() {
        if (!repository.exists()) {
            StsKeyStore keyStore = generator.generate();
            repository.save(keyStore);

            return true;
        } else {
            return false;
        }
    }
}
