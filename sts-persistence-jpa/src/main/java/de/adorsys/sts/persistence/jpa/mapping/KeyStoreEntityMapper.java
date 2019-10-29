package de.adorsys.sts.persistence.jpa.mapping;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.keystore.KeyStoreView;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.keymanagement.api.types.template.NameAndPassword;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyEntry;
import de.adorsys.sts.keymanagement.model.*;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyEntryAttributes;
import de.adorsys.sts.persistence.jpa.entity.JpaKeyStore;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeyStoreEntityMapper {

    private final Juggler juggler;
    private final PasswordCallbackHandler keyPassHandler;
    private final String keystoreName;

    @Autowired
    public KeyStoreEntityMapper(
            Juggler juggler,
            KeyManagementProperties keyManagementProperties
    ) {
        this.juggler = juggler;
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
        UnmodifyableKeystore toPersist = keyStore.getKeyStoreCopy();
        byte[] bytes = toPersist.toBytes(juggler, keyPassHandler::getPassword);

        persistentKeyStore.setName(keystoreName);
        persistentKeyStore.setKeystore(bytes);
        persistentKeyStore.setType(toPersist.getType());
        persistentKeyStore.setLastUpdate(keyStore.getLastUpdate());
    }

    public StsKeyStore mapFromEntity(JpaKeyStore persistentKeyStore, List<JpaKeyEntryAttributes> persistentKeyEntries) {
        KeyStore orig = juggler.serializeDeserialize()
                .deserialize(persistentKeyStore.getKeystore(), keyPassHandler::getPassword);

        Map<String, StsKeyEntry> mappedKeyEntries = mapFromEntities(persistentKeyEntries);

        KeyStore keyStore = upgradeKeyStoreIfNeeded(orig, mappedKeyEntries);
        return StsKeyStore.builder()
                .keyStore(keyStore)
                .view(juggler.readKeys().fromKeyStore(keyStore, id -> keyPassHandler.getPassword()).entries())
                .lastUpdate(persistentKeyStore.getLastUpdate())
                .build();
    }

    private KeyStore upgradeKeyStoreIfNeeded(KeyStore original, Map<String, StsKeyEntry> entries) {
        KeyStoreView view = juggler.readKeys().fromKeyStore(original, id -> keyPassHandler.getPassword());
        for (KeyEntry key : view.entries().all()) {
            if (null != key.getMeta()) {
                continue;
            }

            view.entries().remove(key);
            view.entries().add(ProvidedKeyEntry.builder()
                    .keyTemplate(new NameAndPassword(key.getAlias(), keyPassHandler::getPassword))
                    .entry(key.getEntry())
                    .metadata(entries.get(key.getAlias()))
                    .build()
            );
        }

        return original;
    }

    @SneakyThrows
    private Map<String, StsKeyEntry> mapFromEntities(List<JpaKeyEntryAttributes> persistentKeyEntries) {
        Map<String, StsKeyEntry> mappedKeyEntries = new HashMap<>();

        for (JpaKeyEntryAttributes keyEntryAttributes : persistentKeyEntries) {
            StsKeyEntry mappedKeyEntry = mapFromEntity(keyEntryAttributes);
            mappedKeyEntries.put(mappedKeyEntry.getAlias(), mappedKeyEntry);
        }

        return mappedKeyEntries;
    }

    private StsKeyEntry mapFromEntity(JpaKeyEntryAttributes keyEntryAttributes) {
        return StsKeyEntryImpl.builder()
                .alias(keyEntryAttributes.getAlias())
                .createdAt(keyEntryAttributes.getCreatedAt())
                .notBefore(keyEntryAttributes.getNotBefore())
                .notAfter(keyEntryAttributes.getNotAfter())
                .expireAt(keyEntryAttributes.getExpireAt())
                .validityInterval(keyEntryAttributes.getValidityInterval())
                .legacyInterval(keyEntryAttributes.getLegacyInterval())
                .state(keyEntryAttributes.getState())
                .keyUsage(keyEntryAttributes.getKeyUsage())
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
