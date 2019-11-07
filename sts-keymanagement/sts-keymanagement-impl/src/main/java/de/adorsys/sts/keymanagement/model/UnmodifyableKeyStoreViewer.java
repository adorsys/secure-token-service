package de.adorsys.sts.keymanagement.model;

import lombok.RequiredArgsConstructor;

import java.security.KeyStore;

@RequiredArgsConstructor
public class UnmodifyableKeyStoreViewer {

    private final UnmodifyableKeystore keystore;

    public KeyStore getKeyStore() {
        return keystore.getDelegate();
    }
}
