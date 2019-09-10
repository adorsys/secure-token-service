package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.cryptoutils.KeyConverter;
import de.adorsys.sts.cryptoutils.ServerKeysHolder;

import java.security.KeyStore;

public class KeyConversionService {

    private final String keyStorePassword;

    public KeyConversionService(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public ServerKeysHolder export(KeyStore keyStore) {
        JWKSet privateKeys = KeyConverter.exportPrivateKeys(keyStore, keyStorePassword.toCharArray());
        JWKSet publicKeys = privateKeys.toPublicJWKSet();

        return new ServerKeysHolder(privateKeys, publicKeys);
    }
}
