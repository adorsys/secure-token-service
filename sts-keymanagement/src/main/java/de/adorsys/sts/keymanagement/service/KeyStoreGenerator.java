package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.config.KeyManagementProperties;
import org.adorsys.jjwk.serverkey.KeyStoreUtils;
import org.adorsys.jkeygen.keystore.KeyPairData;
import org.adorsys.jkeygen.keystore.KeystoreBuilder;
import org.adorsys.jkeygen.keystore.SecretKeyData;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.callback.CallbackHandler;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.UUID;

public class KeyStoreGenerator {

    private final KeyPairGenerator encKeyPairGenerator;
    private final KeyPairGenerator signKeyPairGenerator;
    private final SecretKeyGenerator secretKeyGenerator;

    private final String keyStoreType;
    private final String serverKeystoreName;
    private final String serverKeyPairAliasPrefix;
    private final Integer numberOfSignKeyPairs;
    private final Integer numberOfEncKeyPairs;
    private final Integer numberOfSecretKeys;

    private final CallbackHandler keyPassHandler;
    private final CallbackHandler storePassHandler;

    public KeyStoreGenerator(
            KeyPairGenerator encKeyPairGenerator,
            KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            String keyStoreType,
            String serverKeystoreName,
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
        this.serverKeystoreName = serverKeystoreName;
        this.serverKeyPairAliasPrefix = serverKeyPairAliasPrefix;

        this.numberOfSignKeyPairs = numberOfSignKeyPairs;
        this.numberOfEncKeyPairs = numberOfEncKeyPairs;
        this.numberOfSecretKeys = numberOfSecretKeys;

        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
        storePassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
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
        this.serverKeystoreName = keystoreProperties.getName();
        this.serverKeyPairAliasPrefix = keystoreProperties.getAliasPrefix();

        KeyManagementProperties.KeyStoreProperties.KeysProperties keysProperties = keystoreProperties.getKeys();

        this.numberOfSignKeyPairs = keysProperties.getSignKeyPairs().getInitialCount();
        this.numberOfEncKeyPairs = keysProperties.getEncKeyPairs().getInitialCount();
        this.numberOfSecretKeys = keysProperties.getSecretKeys().getInitialCount();

        String password = keystoreProperties.getPassword();
        keyPassHandler = new PasswordCallbackHandler(password.toCharArray());
        storePassHandler = new PasswordCallbackHandler(password.toCharArray());
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
                        storePassHandler
                );

                keystoreBuilder = keystoreBuilder.withKeyEntry(secretKeyData);
            }


            byte[] bs = keystoreBuilder.withStoreId(serverKeystoreName).build(storePassHandler);

            ByteArrayInputStream bis = new ByteArrayInputStream(bs);
            return KeyStoreUtils.loadKeyStore(bis, serverKeystoreName, keyStoreType, storePassHandler);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
