package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.KeySetTemplate;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;
import de.adorsys.sts.keymanagement.model.*;
import de.adorsys.sts.keymanagement.util.DateTimeUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.security.auth.callback.CallbackHandler;
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
            StsKeyEntry<ProvidedKeyPair> signKeyPair = generateSignatureKeyEntryForInstantUsage();
            builder = builder.providedPair(signKeyPair.getKeyEntry());

            signKeyPair = generateSignatureKeyEntryForFutureUsage(signKeyPair.getNotAfter());
            builder = builder.providedPair(signKeyPair.getKeyEntry());
        }

        for (int i = 0; i < encKeyPairsProperties.getInitialCount(); i++) {
            StsKeyEntry<ProvidedKeyPair> encryptionKeyPair = generateEncryptionKeyEntryForInstantUsage();
            builder = builder.providedPair(encryptionKeyPair.getKeyEntry());

            encryptionKeyPair = generateEncryptionKeyEntryForFutureUsage(encryptionKeyPair.getNotAfter());
            builder = builder.providedPair(encryptionKeyPair.getKeyEntry());
        }

        for (int i = 0; i < secretKeyProperties.getInitialCount(); i++) {
            StsKeyEntry<ProvidedKey> secretKey = generateSecretKeyEntryForInstantUsage();
            builder = builder.providedKey(secretKey.getKeyEntry());

            secretKey = generateSecretKeyEntryForFutureUsage(secretKey.getNotAfter());
            builder = builder.providedKey(secretKey.getKeyEntry());
        }

        return StsKeyStore.builder()
                .keyEntries(null)
                .keyStore(
                        juggler.toKeystore().generate(juggler.generateKeys().fromTemplate(builder.build()))
                )
                .lastUpdate(now())
                .build();
    }

    @Override
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

    @Override
    public StsKeyEntry<ProvidedKeyPair> generateSignatureKeyEntryForInstantUsage() {
        ProvidedKeyPair signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKeyPair>builder()
                .alias(signatureKeyPair.generateName())
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

    @Override
    public StsKeyEntry<ProvidedKeyPair> generateSignatureKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKeyPair signatureKeyPair = generateSignKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKeyPair>builder()
                .alias(signatureKeyPair.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(signKeyPairsProperties.getValidityInterval())
                .legacyInterval(signKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Signature)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(signatureKeyPair)
                .build();
    }

    private ProvidedKeyPair generateSignKeyPair() {
        String alias = serverKeyPairAliasPrefix + UUID.randomUUID().toString();
        return signKeyPairGenerator.generateSignatureKey(
                alias,
                keyPassHandler::getPassword
        );
    }

    @Override
    public StsKeyEntry<ProvidedKeyPair> generateEncryptionKeyEntryForInstantUsage() {
        ProvidedKeyPair signatureKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKeyPair>builder()
                .alias(signatureKeyPair.generateName())
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

    @Override
    public StsKeyEntry<ProvidedKeyPair> generateEncryptionKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKeyPair encryptionKeyPair = generateEncryptionKeyPair();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKeyPair>builder()
                .alias(encryptionKeyPair.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(encKeyPairsProperties.getValidityInterval())
                .legacyInterval(encKeyPairsProperties.getLegacyInterval())
                .keyUsage(KeyUsage.Encryption)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(encryptionKeyPair)
                .build();
    }

    private ProvidedKeyPair generateEncryptionKeyPair() {
        String alias = serverKeyPairAliasPrefix + RandomStringUtils.randomAlphanumeric(5).toUpperCase();
        return encKeyPairGenerator.generateEncryptionKey(
                alias,
                keyPassHandler::getPassword
        );
    }

    @Override
    public StsKeyEntry<ProvidedKey> generateSecretKeyEntryForInstantUsage() {
        ProvidedKey secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKey>builder()
                .alias(secretKeyData.generateName())
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

    @Override
    public StsKeyEntry<ProvidedKey> generateSecretKeyEntryForFutureUsage(ZonedDateTime notBefore) {
        ProvidedKey secretKeyData = generateSecretKey();
        ZonedDateTime now = now();

        return StsKeyEntry.<ProvidedKey>builder()
                .alias(secretKeyData.generateName())
                .createdAt(now)
                .notBefore(notBefore)
                .validityInterval(secretKeyProperties.getValidityInterval())
                .legacyInterval(secretKeyProperties.getLegacyInterval())
                .keyUsage(KeyUsage.SecretKey)
                .state(StsKeyEntry.State.CREATED)
                .keyEntry(secretKeyData)
                .build();
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
