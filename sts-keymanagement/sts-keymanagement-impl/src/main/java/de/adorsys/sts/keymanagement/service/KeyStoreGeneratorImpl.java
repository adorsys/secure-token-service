package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.KeySetTemplate;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;
import de.adorsys.sts.keymanagement.model.*;
import de.adorsys.sts.keymanagement.util.DateTimeUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.security.KeyStore;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class KeyStoreGeneratorImpl implements KeyStoreGenerator {

    private final Clock clock;

    private final Juggler juggler;
    private final KeyPairGenerator encKeyPairGenerator;
    private final KeyPairGenerator signKeyPairGenerator;
    private final SecretKeyGenerator secretKeyGenerator;

    private final String keyStoreType;
    private final String serverKeyPairAliasPrefix;

    private final PasswordCallbackHandler keyPassHandler;

    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties encKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties signKeyPairsProperties;
    private final KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties;

    public KeyStoreGeneratorImpl(
            Juggler juggler,
            Clock clock,
            KeyPairGenerator encKeyPairGenerator,
            KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementProperties keyManagementProperties
    ) {
        this.juggler = juggler;
        this.clock = clock;
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

    @Override
    public StsKeyStore generate() {

        KeySetTemplate.KeySetTemplateBuilder builder = KeySetTemplate.builder();

        for (int i = 0; i < signKeyPairsProperties.getInitialCount(); i++) {
            GeneratedStsEntry<ProvidedKeyPair> signKeyPair = generateSignatureKeyEntryForInstantUsage();
            builder = builder.providedPair(signKeyPair.getKey());

            signKeyPair = generateSignatureKeyEntryForFutureUsage(signKeyPair.getEntry().getNotAfter());
            builder = builder.providedPair(signKeyPair.getKey());
        }

        for (int i = 0; i < encKeyPairsProperties.getInitialCount(); i++) {
            GeneratedStsEntry<ProvidedKeyPair> encPair = generateEncryptionKeyEntryForInstantUsage();
            builder = builder.providedPair(encPair.getKey());

            encPair = generateEncryptionKeyEntryForFutureUsage(encPair.getEntry().getNotAfter());
            builder = builder.providedPair(encPair.getKey());
        }

        for (int i = 0; i < secretKeyProperties.getInitialCount(); i++) {
            GeneratedStsEntry<ProvidedKey> secretKey = generateSecretKeyEntryForInstantUsage();
            builder = builder.providedKey(secretKey.getKey());

            secretKey = generateSecretKeyEntryForFutureUsage(secretKey.getEntry().getNotAfter());
            builder = builder.providedKey(secretKey.getKey());
        }

        KeyStore keyStore = juggler.toKeystore()
                .generate(
                        juggler.generateKeys().fromTemplate(builder.build()),
                        keyPassHandler::getPassword
                );

        return StsKeyStore.builder()
                .keyStore(keyStore)
                .view(juggler.readKeys().fromKeyStore(keyStore, id -> keyPassHandler.getPassword()).entries())
                .lastUpdate(now())
                .build();
    }

    @Override
    public GeneratedStsEntry generateKeyEntryForFutureUsage(KeyUsage keyUsage, ZonedDateTime notBefore) {
        GeneratedStsEntry generatedKeyEntry;

        if (keyUsage == KeyUsage.Encryption) {
            generatedKeyEntry = generateEncryptionKeyEntryForFutureUsage(notBefore);
        }
        else if (keyUsage == KeyUsage.Signature) {
            generatedKeyEntry = generateSignatureKeyEntryForFutureUsage(notBefore);
        }
        else if (keyUsage == KeyUsage.SecretKey) {
            generatedKeyEntry = generateSecretKeyEntryForFutureUsage(notBefore);
        } else {
            throw new IllegalArgumentException("unknown KeyUsage: " + keyUsage.name());
        }

        return generatedKeyEntry;
    }

    @Override
    public GeneratedStsEntry<ProvidedKeyPair> generateSignatureKeyEntryForInstantUsage() {
        ProvidedKeyPair signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(signatureKeyPair.generateName())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(signKeyPairsProperties.getValidityInterval())
                .legacyInterval(signKeyPairsProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, signKeyPairsProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, signKeyPairsProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.Signature)
                .state(KeyState.VALID)
                .build();
        return new GeneratedStsEntry<>(
                entry,
                signatureKeyPair.toBuilder().metadata(entry).build()
        );
    }

    @Override
    public GeneratedStsEntry<ProvidedKeyPair> generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKeyPair signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(signatureKeyPair.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(signKeyPairsProperties.getValidityInterval())
                .legacyInterval(signKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Signature)
                .state(KeyState.CREATED)
                .build();

        return new GeneratedStsEntry<>(
                entry,
                signatureKeyPair.toBuilder().metadata(entry).build()
        );
    }

    private ProvidedKeyPair generateSignKeyPair() {
        String alias = serverKeyPairAliasPrefix + UUID.randomUUID().toString();
        return signKeyPairGenerator.generateSignatureKey(
                alias,
                keyPassHandler::getPassword
        );
    }

    @Override
    public GeneratedStsEntry<ProvidedKeyPair> generateEncryptionKeyEntryForInstantUsage() {
        ProvidedKeyPair encryptionKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(encryptionKeyPair.generateName())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(encKeyPairsProperties.getValidityInterval())
                .legacyInterval(encKeyPairsProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, encKeyPairsProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, encKeyPairsProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.Encryption)
                .state(KeyState.VALID)
                .build();
        return new GeneratedStsEntry<>(
                entry,
                encryptionKeyPair.toBuilder().metadata(entry).build()
        );
    }

    @Override
    public GeneratedStsEntry<ProvidedKeyPair> generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKeyPair encryptionKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(encryptionKeyPair.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(encKeyPairsProperties.getValidityInterval())
                .legacyInterval(encKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Encryption)
                .state(KeyState.CREATED)
                .build();

        return new GeneratedStsEntry<>(
                entry,
                encryptionKeyPair.toBuilder().metadata(entry).build()
        );
    }

    private ProvidedKeyPair generateEncryptionKeyPair() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        return encKeyPairGenerator.generateEncryptionKey(
                alias,
                keyPassHandler::getPassword
        );
    }

    @Override
    public GeneratedStsEntry<ProvidedKey> generateSecretKeyEntryForInstantUsage() {
        ProvidedKey secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(secretKeyData.generateName())
                .createdAt(now)
                .notBefore(now)
                .validityInterval(secretKeyProperties.getValidityInterval())
                .legacyInterval(secretKeyProperties.getLegacyInterval())
                .notAfter(DateTimeUtils.addMillis(now, secretKeyProperties.getValidityInterval()))
                .expireAt(DateTimeUtils.addMillis(now, secretKeyProperties.getLegacyInterval()))
                .keyUsage(KeyUsage.SecretKey)
                .state(KeyState.VALID)
                .build();

        return new GeneratedStsEntry<>(
                entry,
                secretKeyData.toBuilder().metadata(entry).build()
        );
    }

    @Override
    public GeneratedStsEntry<ProvidedKey> generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKey secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        StsKeyEntryImpl entry = StsKeyEntryImpl.builder()
                .alias(secretKeyData.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(secretKeyProperties.getValidityInterval())
                .legacyInterval(secretKeyProperties.getLegacyInterval())
                .keyUsage(KeyUsage.SecretKey)
                .state(KeyState.CREATED)
                .build();

        return new GeneratedStsEntry<>(
                entry,
                secretKeyData.toBuilder().metadata(entry).build()
        );
    }

    private ProvidedKey generateSecretKey() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        return secretKeyGenerator.generate(
                alias,
                keyPassHandler::getPassword
        );
    }

    private ZonedDateTime now() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
}
