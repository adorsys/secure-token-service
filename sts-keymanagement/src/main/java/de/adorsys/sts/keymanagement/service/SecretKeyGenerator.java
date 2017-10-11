package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.config.KeyManagementProperties;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.secretkey.SecretKeyBuilder;

import javax.crypto.SecretKey;
import javax.security.auth.callback.CallbackHandler;

public class SecretKeyGenerator {

    private final String secretKeyAlgo;
    private final Integer keySize;

    public SecretKeyGenerator(String secretKeyAlgo, Integer keySize) {
        this.secretKeyAlgo = secretKeyAlgo;
        this.keySize = keySize;
    }

    public SecretKeyGenerator(KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties) {
        this.secretKeyAlgo = secretKeyProperties.getAlgo();
        this.keySize = secretKeyProperties.getSize();
    }

    public SecretKeyData generate(String alias, CallbackHandler secretKeyPassHandler) {
        SecretKey secretKey = new SecretKeyBuilder()
                .withKeyAlg(secretKeyAlgo)
                .withKeyLength(keySize)
                .build();

        return new SecretKeyData(secretKey, alias, secretKeyPassHandler);
    }
}
