package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.*;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.util.DateTimeUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.callback.CallbackHandler;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KeyStoreGenerator {

    private final Clock clock;

    private final KeyPairGenerator encKeyPairGenerator;
    private final KeyPairGenerator signKeyPairGenerator;
    private final SecretKeyGenerator secretKeyGenerator;

    private final KeyStoreType keyStoreType;
    private final String serverKeyPairAliasPrefix;

    private final CallbackHandler keyPassHandler;

    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties encKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties signKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties;

    public KeyStoreGenerator(
            Clock clock,
            KeyPairGenerator encKeyPairGenerator,
            KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementProperties keyManagementProperties
    ) {
        this.clock = clock;
        this.encKeyPairGenerator = encKeyPairGenerator;
        this.signKeyPairGenerator = signKeyPairGenerator;
        this.secretKeyGenerator = secretKeyGenerator;

        KeyManagementProperties.KeyStoreProperties keystoreProperties = keyManagementProperties.getKeystore();

        this.keyStoreType = new KeyStoreType(keystoreProperties.getType());
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
                StsKeyEntry signKeyPair = generateSignatureKeyEntryForInstantUsage();

                keystoreBuilder = keystoreBuilder.withKeyEntry(signKeyPair.getKeyEntry());
                keyEntries.put(signKeyPair.getAlias(), signKeyPair);

                signKeyPair = generateSignatureKeyEntryForFutureUsage(signKeyPair.getNotAfter());

                keystoreBuilder = keystoreBuilder.withKeyEntry(signKeyPair.getKeyEntry());
                keyEntries.put(signKeyPair.getAlias(), signKeyPair);
            }

            for (int i = 0; i < encKeyPairsProperties.getInitialCount(); i++) {
                StsKeyEntry encryptionKeyPair = generateEncryptionKeyEntryForInstantUsage();

                keystoreBuilder = keystoreBuilder.withKeyEntry(encryptionKeyPair.getKeyEntry());
                keyEntries.put(encryptionKeyPair.getAlias(), encryptionKeyPair);

                encryptionKeyPair = generateEncryptionKeyEntryForFutureUsage(encryptionKeyPair.getNotAfter());

                keystoreBuilder = keystoreBuilder.withKeyEntry(encryptionKeyPair.getKeyEntry());
                keyEntries.put(encryptionKeyPair.getAlias(), encryptionKeyPair);
            }

            for (int i = 0; i < secretKeyProperties.getInitialCount(); i++) {
                StsKeyEntry secretKey = generateSecretKeyEntryForInstantUsage();

                keystoreBuilder = keystoreBuilder.withKeyEntry(secretKey.getKeyEntry());
                keyEntries.put(secretKey.getAlias(), secretKey);

                secretKey = generateSecretKeyEntryForFutureUsage(secretKey.getNotAfter());

                keystoreBuilder = keystoreBuilder.withKeyEntry(secretKey.getKeyEntry());
                keyEntries.put(secretKey.getAlias(), secretKey);
            }

            return StsKeyStore.builder()
                    .keyEntries(keyEntries)
                    .keyStore(keystoreBuilder.build())
                    .lastUpdate(now())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public StsKeyEntry generateKeyEntryForFutureUsage(KeyUsage keyUsage, ZonedDateTime notBefore) {
        StsKeyEntry generatedKeyEntry;

        if(keyUsage == KeyUsage.Encryption) {
            generatedKeyEntry = generateEncryptionKeyEntryForFutureUsage(notBefore);
        }
        else if(keyUsage == KeyUsage.Signature) {
            generatedKeyEntry = generateSignatureKeyEntryForFutureUsage(notBefore);
        }
        else if(keyUsage == KeyUsage.SecretKey) {
            generatedKeyEntry = generateSecretKeyEntryForFutureUsage(notBefore);
        } else {
            throw new IllegalArgumentException("unknown KeyUsage: " + keyUsage.name());
        }

        return generatedKeyEntry;
    }

    public StsKeyEntry generateSignatureKeyEntryForInstantUsage() {
        KeyPairEntry signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(signatureKeyPair.getAlias())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(signKeyPairsProperties.getValidityInterval())
                .legacyInterval(signKeyPairsProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, signKeyPairsProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, signKeyPairsProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.Signature)
                .state(StsKeyEntry.State.VALID)
                .keyEntry(signatureKeyPair)
                .build();
    }

    public StsKeyEntry generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        KeyPairEntry signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(signatureKeyPair.getAlias())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(signKeyPairsProperties.getValidityInterval())
                .legacyInterval(signKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Signature)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(signatureKeyPair)
                .build();
    }

    private KeyPairEntry generateSignKeyPair() {
        String alias = serverKeyPairAliasPrefix + UUID.randomUUID().toString();
        return signKeyPairGenerator.generateSignatureKey(
                alias,
                keyPassHandler
        );
    }

    public StsKeyEntry generateEncryptionKeyEntryForInstantUsage() {
        KeyPairEntry signatureKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(signatureKeyPair.getAlias())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(encKeyPairsProperties.getValidityInterval())
                .legacyInterval(encKeyPairsProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, encKeyPairsProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, encKeyPairsProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.Encryption)
                .state(StsKeyEntry.State.VALID)
                .keyEntry(signatureKeyPair)
                .build();
    }

    public StsKeyEntry generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        KeyPairEntry encryptionKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(encryptionKeyPair.getAlias())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(encKeyPairsProperties.getValidityInterval())
                .legacyInterval(encKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Encryption)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(encryptionKeyPair)
                .build();
    }

    private KeyPairEntry generateEncryptionKeyPair() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        return encKeyPairGenerator.generateEncryptionKey(
                alias,
                keyPassHandler
        );
    }

    public StsKeyEntry generateSecretKeyEntryForInstantUsage() {
        SecretKeyEntry secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(secretKeyData.getAlias())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(secretKeyProperties.getValidityInterval())
                .legacyInterval(secretKeyProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, secretKeyProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, secretKeyProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.SecretKey)
                .state(StsKeyEntry.State.VALID)
                .keyEntry(secretKeyData)
                .build();
    }

    public StsKeyEntry generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        SecretKeyEntry secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        return StsKeyEntry.builder()
                .alias(secretKeyData.getAlias())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(secretKeyProperties.getValidityInterval())
                .legacyInterval(secretKeyProperties.getLegacyInterval())
                .keyUsage(KeyUsage.SecretKey)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(secretKeyData)
                .build();
    }

    private SecretKeyEntry generateSecretKey() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        return secretKeyGenerator.generate(
                alias,
                keyPassHandler
        );
    }

    private ZonedDateTime now() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
}
