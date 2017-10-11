package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.config.KeyManagementProperties;
import org.adorsys.jkeygen.keystore.KeyPairData;
import org.adorsys.jkeygen.keystore.KeystoreBuilder;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.callback.CallbackHandler;
import java.security.KeyStore;
import java.util.UUID;

public class KeyStoreGenerator {

    private final KeyPairGenerator encKeyPairGenerator;
    private final KeyPairGenerator signKeyPairGenerator;
    private final SecretKeyGenerator secretKeyGenerator;

    private final String keyStoreType;
    private final String serverKeyPairAliasPrefix;
    private final Integer numberOfSignKeyPairs;
    private final Integer numberOfEncKeyPairs;
    private final Integer numberOfSecretKeys;

    private final CallbackHandler keyPassHandler;

    public KeyStoreGenerator(
            KeyPairGenerator encKeyPairGenerator,
            KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            String keyStoreType,
            String serverKeyPairAliasPrefix,
            Integer numberOfSignKeyPairs,
            Integer numberOfEncKeyPairs,
            Integer numberOfSecretKeys,
            String keyStorePassword
    ) {
        this.encKeyPairGenerator = encKeyPairGenerator;
        this.signKeyPairGenerator = signKeyPairGenerator;
        this.secretKeyGenerator = secretKeyGenerator;

        this.keyStoreType = keyStoreType;
        this.serverKeyPairAliasPrefix = serverKeyPairAliasPrefix;

        this.numberOfSignKeyPairs = numberOfSignKeyPairs;
        this.numberOfEncKeyPairs = numberOfEncKeyPairs;
        this.numberOfSecretKeys = numberOfSecretKeys;

        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
    }

    public KeyStoreGenerator(
            KeyPairGenerator encKeyPairGenerator,
            KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementProperties keyManagementProperties
    ) {
        this.encKeyPairGenerator = encKeyPairGenerator;
        this.signKeyPairGenerator = signKeyPairGenerator;
        this.secretKeyGenerator = secretKeyGenerator;

        KeyManagementProperties.KeyStoreProperties keystoreProperties = keyManagementProperties.getKeystore();

        this.keyStoreType = keystoreProperties.getType();
        this.serverKeyPairAliasPrefix = keystoreProperties.getAliasPrefix();

        KeyManagementProperties.KeyStoreProperties.KeysProperties keysProperties = keystoreProperties.getKeys();

        this.numberOfSignKeyPairs = keysProperties.getSignKeyPairs().getInitialCount();
        this.numberOfEncKeyPairs = keysProperties.getEncKeyPairs().getInitialCount();
        this.numberOfSecretKeys = keysProperties.getSecretKeys().getInitialCount();

        String password = keystoreProperties.getPassword();
        keyPassHandler = new PasswordCallbackHandler(password.toCharArray());
    }

    public KeyStore generate() {
        try {
            KeystoreBuilder keystoreBuilder = new KeystoreBuilder().withStoreType(keyStoreType);
            for (int i = 0; i < numberOfSignKeyPairs; i++) {
                KeyPairData signatureKeyPair = signKeyPairGenerator.generateSignatureKey(
                        serverKeyPairAliasPrefix + UUID.randomUUID().toString(),
                        keyPassHandler
                );

                keystoreBuilder = keystoreBuilder.withKeyEntry(signatureKeyPair);
            }
            for (int i = 0; i < numberOfEncKeyPairs; i++) {
                KeyPairData signatureKeyPair = encKeyPairGenerator.generateEncryptionKey(
                        serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
                        keyPassHandler
                );

                keystoreBuilder = keystoreBuilder.withKeyEntry(signatureKeyPair);
            }
            for (int i = 0; i < numberOfSecretKeys; i++) {
                SecretKeyData secretKeyData = secretKeyGenerator.generate(
                        serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase(),
                        keyPassHandler
                );

                keystoreBuilder = keystoreBuilder.withKeyEntry(secretKeyData);
            }

            return keystoreBuilder.build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
