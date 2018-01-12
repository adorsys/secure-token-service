package de.adorsys.sts.keymanagement.service;

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

    private final Integer minimumEncryptionKeys;
    private final Integer minimumSignatureKeys;
    private final Integer minimumSecretKeys;

    public KeyRotationService(
            KeyStoreFilter keyStoreFilter,
            KeyStoreGenerator keyStoreGenerator,
            KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties encryptionKeyPairRotationProperties,
            KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties signatureKeyPairRotationProperties,
            KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyRotationProperties secretKeyRotationProperties
    ) {
        this.keyStoreFilter = keyStoreFilter;
        this.keyStoreGenerator = keyStoreGenerator;

        this.minimumEncryptionKeys = encryptionKeyPairRotationProperties.getMinKeys();
        this.minimumSignatureKeys = signatureKeyPairRotationProperties.getMinKeys();
        this.minimumSecretKeys = secretKeyRotationProperties.getMinKeys();
    }

    public KeyRotationResult rotate(StsKeyStore stsKeyStore) {
        Collection<StsKeyEntry> actualKeys = stsKeyStore.getKeyEntries().values();
        Collection<StsKeyEntry> keyEntries = new ArrayList<>(actualKeys);

        List<String> removedKeys = keyEntries.stream()
                .filter(keyStoreFilter::isInvalid)
                .map(k -> removeKey(stsKeyStore, k))
                .collect(Collectors.toList());

        List<String> generatedKeyAliases = generateAndAddMissingKeys(stsKeyStore);

        return KeyRotationResult.builder()
                .generatedKeys(generatedKeyAliases)
                .removedKeys(removedKeys)
                .build();
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

        generatedKeys.addAll(generateMissingEncryptionKeys(actualKeys));
        generatedKeys.addAll(generateMissingSignatureKeys(actualKeys));
        generatedKeys.addAll(generateMissingSecretKeys(actualKeys));

        return generatedKeys;
    }

    private List<StsKeyEntry> generateMissingEncryptionKeys(Collection<StsKeyEntry> actualKeys) {
        List<StsKeyEntry> generatedKeys = new ArrayList<>();

        long countOfValidEncryptionKeyPairs = actualKeys.stream()
                .filter(keyStoreFilter::isValid)
                .filter(k -> k.getKeyUsage() == KeyUsage.Encryption)
                .count();

        for(int i = 0; i < minimumEncryptionKeys - countOfValidEncryptionKeyPairs; i++) {
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

        for(int i = 0; i < minimumSignatureKeys - countOfValidSignatureKeyPairs; i++) {
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

        for(int i = 0; i < minimumSecretKeys - countOfValidSecretKeys; i++) {
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
