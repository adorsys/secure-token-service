package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.cryptoutils.KeyConverter;
import de.adorsys.sts.cryptoutils.ServerKeysHolder;
import de.adorsys.sts.keymanagement.model.ServerKeysHolder;

import java.security.KeyStore;

public class KeyConversionServiceImpl implements KeyConversionService {

    private final String keyStorePassword;

    public KeyConversionServiceImpl(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public ServerKeysHolder export(KeyStore keyStore) {
        JWKSet privateKeys = KeyConverter.exportPrivateKeys(keyStore, keyStorePassword.toCharArray());
        JWKSet publicKeys = privateKeys.toPublicJWKSet();

        return new ServerKeysHolder(privateKeys, publicKeys);
    }
}
