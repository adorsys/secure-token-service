package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.config.KeyManagementRotationProperties;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class KeyRotationService {

    private final KeyStoreFilter keyStoreFilter;
    private final KeyStoreGenerator keyStoreGenerator;

    private final KeyManagementRotationProperties.KeyRotationProperties encryptionKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties signatureKeyPairRotationProperties;
    private final KeyManagementRotationProperties.KeyRotationProperties secretKeyRotationProperties;

    public KeyRotationService(
            KeyStoreFilter keyStoreFilter,
            KeyStoreGenerator keyStoreGenerator,
            KeyManagementRotationProperties rotationProperties
    ) {
        this.keyStoreFilter = keyStoreFilter;
        this.keyStoreGenerator = keyStoreGenerator;

        this.encryptionKeyPairRotationProperties = rotationProperties.getEncKeyPairs();
        this.signatureKeyPairRotationProperties = rotationProperties.getSignKeyPairs();
        this.secretKeyRotationProperties = rotationProperties.getSecretKeys();
    }

    public KeyRotationResult rotate(StsKeyStore stsKeyStore) {
        List<String> removedKeys = removeExpiredKeys(stsKeyStore);
        List<String> generatedKeyAliases = generateAndAddMissingKeys(stsKeyStore);

        return KeyRotationResult.builder()
                .generatedKeys(generatedKeyAliases)
                .removedKeys(removedKeys)
                .build();
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
        Collection<StsKeyEntry> actualKeys = stsKeyStore.getKeyEntries().values();
        Collection<StsKeyEntry> copiedKeyEntries = new ArrayList<>(actualKeys);

        return copiedKeyEntries.stream()
                .filter(keyStoreFilter::isInvalid)
                .filter(k -> k.getKeyUsage() == keyUsage)
                .map(k -> removeKey(stsKeyStore, k))
                .collect(Collectors.toList());
    }

    private List<String> generateAndAddMissingKeys(StsKeyStore stsKeyStore) {
        Collection<StsKeyEntry> actualKeys = stsKeyStore.getKeyEntries().values();

        List<StsKeyEntry> generatedKeys = generateMissingKeys(actualKeys);
        List<String> generatedKeyAliases = new ArrayList<>();

        for(StsKeyEntry generatedKey : generatedKeys) {
            stsKeyStore.addKey(generatedKey);
            generatedKeyAliases.add(generatedKey.getAlias());
        }

        return generatedKeyAliases;
    }

    private List<StsKeyEntry> generateMissingKeys(Collection<StsKeyEntry> actualKeys) {
        List<StsKeyEntry> generatedKeys = new ArrayList<>();

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

    private List<StsKeyEntry> generateMissingEncryptionKeys(Collection<StsKeyEntry> actualKeys) {
        List<StsKeyEntry> generatedKeys = new ArrayList<>();

        long countOfValidEncryptionKeyPairs = actualKeys.stream()
                .filter(keyStoreFilter::isValid)
                .filter(k -> k.getKeyUsage() == KeyUsage.Encryption)
                .count();

        for(int i = 0; i < encryptionKeyPairRotationProperties.getMinKeys() - countOfValidEncryptionKeyPairs; i++) {
            StsKeyEntry generatedKeyAlias = generateKey(KeyUsage.Encryption);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private List<StsKeyEntry> generateMissingSignatureKeys(Collection<StsKeyEntry> actualKeys) {
        List<StsKeyEntry> generatedKeys = new ArrayList<>();

        long countOfValidSignatureKeyPairs = actualKeys.stream()
                .filter(keyStoreFilter::isValid)
                .filter(k -> k.getKeyUsage() == KeyUsage.Signature)
                .count();

        for(int i = 0; i < signatureKeyPairRotationProperties.getMinKeys() - countOfValidSignatureKeyPairs; i++) {
            StsKeyEntry generatedKeyAlias = generateKey(KeyUsage.Signature);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private List<StsKeyEntry> generateMissingSecretKeys(Collection<StsKeyEntry> actualKeys) {
        List<StsKeyEntry> generatedKeys = new ArrayList<>();

        long countOfValidSecretKeys = actualKeys.stream()
                .filter(keyStoreFilter::isValid)
                .filter(k -> k.getKeyUsage() == KeyUsage.SecretKey)
                .count();

        for(int i = 0; i < secretKeyRotationProperties.getMinKeys() - countOfValidSecretKeys; i++) {
            StsKeyEntry generatedKeyAlias = generateKey(KeyUsage.SecretKey);
            generatedKeys.add(generatedKeyAlias);
        }

        return generatedKeys;
    }

    private String removeKey(StsKeyStore keyStore, StsKeyEntry stsKeyEntry) {
        String alias = stsKeyEntry.getAlias();
        keyStore.removeKey(alias);

        return alias;
    }

    private StsKeyEntry generateKey(KeyUsage keyUsage) {
        StsKeyEntry stsKeyEntry;

        if(keyUsage == KeyUsage.Signature) {
            stsKeyEntry = keyStoreGenerator.generateSignKeyPair();
        } else if(keyUsage == KeyUsage.Encryption) {
            stsKeyEntry = keyStoreGenerator.generateEncryptionKeyPair();
        } else if(keyUsage == KeyUsage.SecretKey) {
            stsKeyEntry = keyStoreGenerator.generateSecretKey();
        } else {
            throw new RuntimeException("Unknown KeyUsage: " + keyUsage);
        }

        return stsKeyEntry;
    }

    @Getter
    @Builder
    public static class KeyRotationResult {

        private final List<String> removedKeys;
        private final List<String> generatedKeys;
    }
}
