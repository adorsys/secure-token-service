package de.adorsys.sts.persistence.jpa.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.adorsys.sts.cryptoutils.KeyEntry;
import de.adorsys.sts.cryptoutils.KeyStoreService;
import de.adorsys.sts.cryptoutils.KeyStoreType;
import de.adorsys.sts.cryptoutils.PasswordCallbackHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyEntryAttributes;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyStore;

@Component
public class KeyStoreEntityMapper {

    private final PasswordCallbackHandler keyPassHandler;
    private final String keystoreName;

    @Autowired
    public KeyStoreEntityMapper(
            KeyManagementProperties keyManagementProperties
    ) {
        String keyStorePassword = keyManagementProperties.getKeystore().getPassword();
        keyPassHandler = new PasswordCallbackHandler(keyStorePassword.toCharArray());
        keystoreName = keyManagementProperties.getKeystore().getName();
    }

    public JpaKeyStore mapToEntity(StsKeyStore keyStore) {
        JpaKeyStore persistentKeyStore = new JpaKeyStore();

        mapIntoEntity(keyStore, persistentKeyStore);

        return persistentKeyStore;
    }

    public void mapIntoEntity(StsKeyStore keyStore, JpaKeyStore persistentKeyStore) {
        byte[] bytes = KeyStoreService.toByteArray(keyStore.getKeyStore(), keystoreName, keyPassHandler);

        persistentKeyStore.setName(keystoreName);
        persistentKeyStore.setKeystore(bytes);
        persistentKeyStore.setType(keyStore.getKeyStore().getType());
        persistentKeyStore.setLastUpdate(keyStore.getLastUpdate());
    }

    public StsKeyStore mapFromEntity(JpaKeyStore persistentKeyStore, List<JpaKeyEntryAttributes> persistentKeyEntries) {
        java.security.KeyStore keyStore = KeyStoreService.loadKeyStore(persistentKeyStore.getKeystore(), keystoreName, new KeyStoreType(persistentKeyStore.getType()), keyPassHandler);

        Map<String, StsKeyEntry> mappedKeyEntries = mapFromEntities(keyStore, persistentKeyEntries);

        return StsKeyStore.builder()
                .keyStore(keyStore)
                .keyEntries(mappedKeyEntries)
                .lastUpdate(persistentKeyStore.getLastUpdate())
                .build();
    }

    private Map<String, StsKeyEntry> mapFromEntities(java.security.KeyStore keyStore, List<JpaKeyEntryAttributes> persistentKeyEntries) {
        Map<String, StsKeyEntry> mappedKeyEntries = new HashMap<>();
        Map<String, KeyEntry> keyEntries = KeyStoreService.loadEntryMap(keyStore, new KeyStoreService.SimplePasswordProvider(keyPassHandler));

        for (JpaKeyEntryAttributes keyEntryAttributes : persistentKeyEntries) {
            KeyEntry keyEntry = keyEntries.get(keyEntryAttributes.getAlias());

            StsKeyEntry mappedKeyEntry = mapFromEntity(keyEntry, keyEntryAttributes);
            mappedKeyEntries.put(mappedKeyEntry.getAlias(), mappedKeyEntry);
        }

        return mappedKeyEntries;
    }

    private StsKeyEntry mapFromEntity(KeyEntry keyEntry, JpaKeyEntryAttributes keyEntryAttributes) {
        return StsKeyEntry.builder()
                .alias(keyEntryAttributes.getAlias())
                .createdAt(keyEntryAttributes.getCreatedAt())
                .notBefore(keyEntryAttributes.getNotBefore())
                .notAfter(keyEntryAttributes.getNotAfter())
                .expireAt(keyEntryAttributes.getExpireAt())
                .validityInterval(keyEntryAttributes.getValidityInterval())
                .legacyInterval(keyEntryAttributes.getLegacyInterval())
                .state(keyEntryAttributes.getState())
                .keyUsage(keyEntryAttributes.getKeyUsage())

                .keyEntry(keyEntry)

                .build();
    }

    public void mapIntoEntity(StsKeyEntry stsKeyEntry, JpaKeyEntryAttributes keyEntryAttributes) {
        keyEntryAttributes.setAlias(stsKeyEntry.getAlias());
        keyEntryAttributes.setCreatedAt(stsKeyEntry.getCreatedAt());
        keyEntryAttributes.setNotBefore(stsKeyEntry.getNotBefore());
        keyEntryAttributes.setNotAfter(stsKeyEntry.getNotAfter());
        keyEntryAttributes.setExpireAt(stsKeyEntry.getExpireAt());
        keyEntryAttributes.setValidityInterval(stsKeyEntry.getValidityInterval());
        keyEntryAttributes.setLegacyInterval(stsKeyEntry.getLegacyInterval());
        keyEntryAttributes.setState(stsKeyEntry.getState());
        keyEntryAttributes.setKeyUsage(stsKeyEntry.getKeyUsage());
    }
}
