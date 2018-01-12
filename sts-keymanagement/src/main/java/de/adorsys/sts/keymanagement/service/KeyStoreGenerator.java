package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import org.adorsys.jkeygen.keystore.KeyPairEntry;
import org.adorsys.jkeygen.keystore.KeystoreBuilder;
import org.adorsys.jkeygen.keystore.SecretKeyEntry;
import org.adorsys.jkeygen.pwd.PasswordCallbackHandler;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.callback.CallbackHandler;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyStoreGenerator {

    private final KeyPairGenerator encKeyPairGenerator;
    private final KeyPairGenerator signKeyPairGenerator;
    private final SecretKeyGenerator secretKeyGenerator;

    private final String keyStoreType;
    private final String serverKeyPairAliasPrefix;

    private final CallbackHandler keyPassHandler;

    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties encKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties signKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties;

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

        String password = keystoreProperties.getPassword();

        encKeyPairsProperties = keyManagementProperties.getKeystore().getKeys().getEncKeyPairs();
        signKeyPairsProperties = keyManagementProperties.getKeystore().getKeys().getSignKeyPairs();
        secretKeyProperties = keyManagementProperties.getKeystore().getKeys().getSecretKeys();

        keyPassHandler = new PasswordCallbackHandler(password.toCharArray());
    }

    public StsKeyStore generate() {
        Map<String, StsKeyEntry> keyEntries = new HashMap<>();

        try {
            KeystoreBuilder keystoreBuilder = new KeystoreBuilder().withStoreType(keyStoreType);

            for (int i = 0; i < signKeyPairsProperties.getInitialCount(); i++) {
                StsKeyEntry signKeyPair = generateSignKeyPair();

                keystoreBuilder = keystoreBuilder.withKeyEntry(signKeyPair.getKeyEntry());
                keyEntries.put(signKeyPair.getAlias(), signKeyPair);
            }

            for (int i = 0; i < encKeyPairsProperties.getInitialCount(); i++) {
                StsKeyEntry encryptionKeyPair = generateEncryptionKeyPair();

                keystoreBuilder = keystoreBuilder.withKeyEntry(encryptionKeyPair.getKeyEntry());
                keyEntries.put(encryptionKeyPair.getAlias(), encryptionKeyPair);
            }

            for (int i = 0; i < secretKeyProperties.getInitialCount(); i++) {
                StsKeyEntry secretKey = generateSecretKey();

                keystoreBuilder = keystoreBuilder.withKeyEntry(secretKey.getKeyEntry());
                keyEntries.put(secretKey.getAlias(), secretKey);
            }

            return StsKeyStore.builder()
                    .keyEntries(keyEntries)
                    .keyStore(keystoreBuilder.build())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public StsKeyEntry generateSignKeyPair() {
        String alias = serverKeyPairAliasPrefix + UUID.randomUUID().toString();
        KeyPairEntry signatureKeyPair = signKeyPairGenerator.generateSignatureKey(
                alias,
                keyPassHandler
        );

        KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties signKeyRotationProperties = signKeyPairsProperties.getRotation();

        return StsKeyEntry.builder()
                .alias(alias)
                .createdAt(now())
                .validityInterval(signKeyRotationProperties.getValidityInterval())
                .legacyInterval(signKeyRotationProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Signature)
                .keyEntry(signatureKeyPair)
                .build();
    }

    public StsKeyEntry generateEncryptionKeyPair() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        KeyPairEntry signatureKeyPair = encKeyPairGenerator.generateEncryptionKey(
                alias,
                keyPassHandler
        );

        KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties encKeyRotationProperties = encKeyPairsProperties.getRotation();

        return StsKeyEntry.builder()
                .alias(alias)
                .createdAt(now())
                .validityInterval(encKeyRotationProperties.getValidityInterval())
                .legacyInterval(encKeyRotationProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Encryption)
                .keyEntry(signatureKeyPair)
                .build();
    }

    public StsKeyEntry generateSecretKey() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        SecretKeyEntry secretKeyData = secretKeyGenerator.generate(
                alias,
                keyPassHandler
        );

        KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties secretKeyRotationProperties = secretKeyProperties.getRotation();

        return StsKeyEntry.builder()
                .alias(alias)
                .createdAt(now())
                .validityInterval(secretKeyRotationProperties.getValidityInterval())
                .legacyInterval(secretKeyRotationProperties.getLegacyInterval())
                .keyUsage(KeyUsage.SecretKey)
                .keyEntry(secretKeyData)
                .build();
    }

    private ZonedDateTime now() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }
}
