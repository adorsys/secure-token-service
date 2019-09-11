package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.SecretKeyBuilder;
import de.adorsys.sts.cryptoutils.SecretKeyData;
import de.adorsys.sts.cryptoutils.SecretKeyEntry;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

public class SecretKeyGeneratorImpl implements SecretKeyGenerator {

    private final String secretKeyAlgo;
    private final Integer keySize;

    public SecretKeyGeneratorImpl(String secretKeyAlgo, Integer keySize) {
        this.secretKeyAlgo = secretKeyAlgo;
        this.keySize = keySize;
    }

    public SecretKeyGeneratorImpl(KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties) {
        this.secretKeyAlgo = secretKeyProperties.getAlgo();
        this.keySize = secretKeyProperties.getSize();
    }

    @Override
    public SecretKeyEntry generate(String alias, CallbackHandler secretKeyPassHandler) {
        SecretKey secretKey = new SecretKeyBuilder()
                .withKeyAlg(secretKeyAlgo)
                .withKeyLength(keySize)
                .build();

        return SecretKeyData.builder()
                .secretKey(secretKey)
                .alias(alias)
                .passwordSource(secretKeyPassHandler)
                .build();
    }
}
