package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import de.adorsys.sts.keymanagement.model.*;
import de.adorsys.sts.keymanagement.util.DateTimeUtils;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeyRotationServiceImpl implements KeyRotationService {

    private final KeyStoreGenerator keyStoreGenerator;
    private final Clock clock;
    private final KeyManagementRotationProperties.KeyRotationProperties encryptionKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties signatureKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties secretKeyRotationProperties;

    public KeyRotationServiceImpl(
            KeyStoreGenerator keyStoreGenerator,
            Clock clock,
            KeyManagementRotationProperties rotationProperties
    ) {
        this.keyStoreGenerator = keyStoreGenerator;
        this.clock = clock;

        this.encryptionKeyPairRotationProperties = rotationProperties.getEncKeyPairs();
        this.signatureKeyPairRotationProperties = rotationProperties.getSignKeyPairs();
        this.secretKeyRotationProperties = rotationProperties.getSecretKeys();
    }

    @Override
    public KeyRotationResult rotate(StsKeyStore stsKeyStore) {
        KeyStateUpdates keyStateUpdates = updateKeyStates(stsKeyStore);
        List<String> createdFutureKeys = createKeysForFutureUsage(stsKeyStore, keyStateUpdates);
        List<String> removedKeys = removeExpiredKeys(stsKeyStore);
        List<String> generatedKeyAliases = generateAndAddMissingKeys(stsKeyStore);

        return KeyRotationResult.builder()
                .generatedKeys(generatedKeyAliases)
                .removedKeys(removedKeys)
                .futureKeys(createdFutureKeys)
                .build();
    }

    private List<String> createKeysForFutureUsage(StsKeyStore stsKeyStore, KeyStateUpdates keyStateUpdates) {
        List<String> generatedKeyAliases = new ArrayList<>();

        for(StsKeyEntry keyEntry : keyStateUpdates.newValidKeys) {
            GeneratedStsEntry generatedKeyEntry = keyStoreGenerator.generateKeyEntryForFutureUsage(
                    keyEntry.getKeyUsage(), keyEntry.getNotAfter()
            );

            stsKeyStore.getView().add(generatedKeyEntry.getKey());
            generatedKeyAliases.add(generatedKeyEntry.getEntry().getAlias());
        }

        return generatedKeyAliases;
    }

    private KeyStateUpdates updateKeyStates(StsKeyStore stsKeyStore) {
        KeyStateUpdates updates = new KeyStateUpdates();
        ZonedDateTime now = now();

        if(encryptionKeyPairRotationProperties.isEnabled()) {
            KeyStateUpdates keyStateUpdates = updateEncryptionKeyEntryStates(stsKeyStore, now);
            updates.merge(keyStateUpdates);
        }
        if(signatureKeyPairRotationProperties.isEnabled()) {
            KeyStateUpdates keyStateUpdates = updateSignatureKeyEntryStates(stsKeyStore, now);
            updates.merge(keyStateUpdates);
        }
        if(secretKeyRotationProperties.isEnabled()) {
            KeyStateUpdates keyStateUpdates = updateSecretKeyEntryStates(stsKeyStore, now);
            updates.merge(keyStateUpdates);
        }

        return updates;
    }

    private KeyStateUpdates updateEncryptionKeyEntryStates(StsKeyStore stsKeyStore, ZonedDateTime now) {
        List<StsKeyEntry> encryptionKeyEntries = stsKeyStore.getEntries().values().stream()
                .filter(k -> k.getKeyUsage() == KeyUsage.Encryption)
                .collect(Collectors.toList());

        return updateKeyEntryStatesForCollection(now, encryptionKeyEntries);
    }

    private KeyStateUpdates updateSignatureKeyEntryStates(StsKeyStore stsKeyStore, ZonedDateTime now) {
        List<StsKeyEntry> encryptionKeyEntries = stsKeyStore.getEntries().values().stream()
                .filter(k -> k.getKeyUsage() == KeyUsage.Signature)
                .collect(Collectors.toList());

        return updateKeyEntryStatesForCollection(now, encryptionKeyEntries);
    }

    private KeyStateUpdates updateSecretKeyEntryStates(StsKeyStore stsKeyStore, ZonedDateTime now) {
        List<StsKeyEntry> encryptionKeyEntries = stsKeyStore.getEntries().values().stream()
                .filter(k -> k.getKeyUsage() == KeyUsage.SecretKey)
                .collect(Collectors.toList());

        return updateKeyEntryStatesForCollection(now, encryptionKeyEntries);
    }

    private KeyStateUpdates updateKeyEntryStatesForCollection(ZonedDateTime now, List<StsKeyEntry> encryptionKeyEntries) {
        KeyStateUpdates updates = new KeyStateUpdates();

        List<StsKeyEntry> keysToBeValid = encryptionKeyEntries.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.CREATED)
                .filter(k -> k.getNotBefore().isBefore(now))
                .collect(Collectors.toList());

        for(StsKeyEntry keyEntry : keysToBeValid) {
            ZonedDateTime notAfter = DateTimeUtils.addMillis(now, keyEntry.getValidityInterval());

            keyEntry.setNotAfter(notAfter);
            keyEntry.setExpireAt(DateTimeUtils.addMillis(notAfter, keyEntry.getValidityInterval()));

            keyEntry.setState(StsKeyEntry.State.VALID);
        }

        updates.newValidKeys = keysToBeValid;

        List<StsKeyEntry> keysToBeLegacy = encryptionKeyEntries.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.VALID)
                .filter(k -> now.isAfter(k.getNotAfter()))
                .collect(Collectors.toList());

        for(StsKeyEntry keyEntry : keysToBeLegacy) {
            keyEntry.setState(StsKeyEntry.State.LEGACY);
        }

        updates.newLegacyKeys = keysToBeLegacy;

        List<StsKeyEntry> keysToBeExpired = encryptionKeyEntries.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.LEGACY)
                .filter(k -> now.isAfter(k.getExpireAt()))
                .collect(Collectors.toList());

        for(StsKeyEntry keyEntry : keysToBeExpired) {
            keyEntry.setState(StsKeyEntry.State.EXPIRED);
        }

        updates.newExpiredKeys = keysToBeExpired;

        return updates;
    }

    private static class KeyStateUpdates {
        public List<StsKeyEntry> newValidKeys = new ArrayList<>();
        public List<StsKeyEntry> newLegacyKeys = new ArrayList<>();
        public List<StsKeyEntry> newExpiredKeys = new ArrayList<>();

        public void merge(KeyStateUpdates other) {
            newValidKeys.addAll(other.newValidKeys);
            newLegacyKeys.addAll(other.newLegacyKeys);
            newExpiredKeys.addAll(other.newExpiredKeys);
        }
    }

    private List<String> removeExpiredKeys(StsKeyStore stsKeyStore) {
        List<String> removedKeys = new ArrayList<>();

        if(encryptionKeyPairRotationProperties.isEnabled()) {
            removedKeys.addAll(removeExpiredKeys(stsKeyStore, KeyUsage.Encryption));
        }
        if(signatureKeyPairRotationProperties.isEnabled()) {
            removedKeys.addAll(removeExpiredKeys(stsKeyStore, KeyUsage.Signature));
        }
        if(secretKeyRotationProperties.isEnabled()) {
            removedKeys.addAll(removeExpiredKeys(stsKeyStore, KeyUsage.SecretKey));
        }

        return removedKeys;
    }

    private List<String> removeExpiredKeys(StsKeyStore stsKeyStore, KeyUsage keyUsage) {
        Collection<StsKeyEntry> actualKeys = stsKeyStore.getEntries().values();
        Collection<StsKeyEntry> copiedKeyEntries = new ArrayList<>(actualKeys);

        return copiedKeyEntries.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.EXPIRED)
                .filter(k -> k.getKeyUsage() == keyUsage)
                .map(k -> removeKey(stsKeyStore, k))
                .collect(Collectors.toList());
    }

    private List<String> generateAndAddMissingKeys(StsKeyStore stsKeyStore) {
        Collection<StsKeyEntry> actualKeys = stsKeyStore.getEntries().values();

        List<GeneratedStsEntry> generatedKeys = generateMissingKeys(actualKeys);
        List<String> generatedKeyAliases = new ArrayList<>();

        for (GeneratedStsEntry generatedKey : generatedKeys) {
            stsKeyStore.getView().add(generatedKey.getKey());
            generatedKeyAliases.add(generatedKey.getEntry().getAlias());
        }

        return generatedKeyAliases;
    }

    private List<GeneratedStsEntry> generateMissingKeys(Collection<StsKeyEntry> actualKeys) {
        List<GeneratedStsEntry> generatedKeys = new ArrayList<>();

        if(encryptionKeyPairRotationProperties.isEnabled()) {
            generatedKeys.addAll(generateMissingEncryptionKeys(actualKeys));
        }
        if(signatureKeyPairRotationProperties.isEnabled()) {
            generatedKeys.addAll(generateMissingSignatureKeys(actualKeys));
        }
        if(secretKeyRotationProperties.isEnabled()) {
            generatedKeys.addAll(generateMissingSecretKeys(actualKeys));
        }

        return generatedKeys;
    }

    private List<GeneratedStsEntry> generateMissingEncryptionKeys(Collection<StsKeyEntry> actualKeys) {
        List<GeneratedStsEntry> generatedKeys = new ArrayList<>();

        long countOfValidEncryptionKeyPairs = actualKeys.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.VALID)
                .filter(k -> k.getKeyUsage() == KeyUsage.Encryption)
                .count();

        for(int i = 0; i < encryptionKeyPairRotationProperties.getMinKeys() - countOfValidEncryptionKeyPairs; i++) {
            GeneratedStsEntry generatedKeyAlias = generateKey(KeyUsage.Encryption);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private List<GeneratedStsEntry> generateMissingSignatureKeys(Collection<StsKeyEntry> actualKeys) {
        List<GeneratedStsEntry> generatedKeys = new ArrayList<>();

        long countOfValidSignatureKeyPairs = actualKeys.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.VALID)
                .filter(k -> k.getKeyUsage() == KeyUsage.Signature)
                .count();

        for(int i = 0; i < signatureKeyPairRotationProperties.getMinKeys() - countOfValidSignatureKeyPairs; i++) {
            GeneratedStsEntry generatedKeyAlias = generateKey(KeyUsage.Signature);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private List<GeneratedStsEntry> generateMissingSecretKeys(Collection<StsKeyEntry> actualKeys) {
        List<GeneratedStsEntry> generatedKeys = new ArrayList<>();

        long countOfValidSecretKeys = actualKeys.stream()
                .filter(k -> k.getState() == StsKeyEntry.State.VALID)
                .filter(k -> k.getKeyUsage() == KeyUsage.SecretKey)
                .count();

        for(int i = 0; i < secretKeyRotationProperties.getMinKeys() - countOfValidSecretKeys; i++) {
            GeneratedStsEntry generatedKeyAlias = generateKey(KeyUsage.SecretKey);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private String removeKey(StsKeyStore keyStore, StsKeyEntry stsKeyEntry) {
        String alias = stsKeyEntry.getAlias();
        keyStore.getView().removeById(alias);
        return alias;
    }

    private GeneratedStsEntry generateKey(KeyUsage keyUsage) {
        GeneratedStsEntry stsKeyEntry;

        if(keyUsage == KeyUsage.Signature) {
            stsKeyEntry = keyStoreGenerator.generateSignatureKeyEntryForInstantUsage();
        } else if(keyUsage == KeyUsage.Encryption) {
            stsKeyEntry = keyStoreGenerator.generateEncryptionKeyEntryForInstantUsage();
        } else if(keyUsage == KeyUsage.SecretKey) {
            stsKeyEntry = keyStoreGenerator.generateSecretKeyEntryForInstantUsage();
        } else {
            throw new IllegalArgumentException("Unknown KeyUsage: " + keyUsage);
        }

        return stsKeyEntry;
    }

    private ZonedDateTime now() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
}
