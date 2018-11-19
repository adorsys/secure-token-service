package de.adorsys.sts.persistence.mongo.mapper;

import de.adorsys.sts.cryptoutils.KeyEntry;
import de.adorsys.sts.cryptoutils.KeyStoreService;
import de.adorsys.sts.cryptoutils.KeyStoreType;
import de.adorsys.sts.cryptoutils.PasswordCallbackHandler;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.mongo.entity.KeyEntryAttributesEntity;
import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class KeyStoreEntityMapper {

    private static final ZonedDateTime DEFAULT_LAST_UPDATE = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);
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

    public KeyStoreEntity mapToEntity(StsKeyStore keyStore) {
        KeyStoreEntity persistentKeyStore = new KeyStoreEntity();

        mapIntoEntity(keyStore, persistentKeyStore);

        return persistentKeyStore;
    }

    public void mapIntoEntity(StsKeyStore keyStore, KeyStoreEntity persistentKeyStore) {
        byte[] bytes = KeyStoreService.toByteArray(keyStore.getKeyStore(), keystoreName, keyPassHandler);

        persistentKeyStore.setName(keystoreName);
        persistentKeyStore.setKeystore(bytes);
        persistentKeyStore.setType(keyStore.getKeyStore().getType());
        persistentKeyStore.setLastUpdate(convert(keyStore.getLastUpdate()));

        Map<String, KeyEntryAttributesEntity> mappedEntryAttributes = mapToEntityMap(keyStore.getKeyEntries());
        persistentKeyStore.setEntries(mappedEntryAttributes);
    }

    private Map<String, KeyEntryAttributesEntity> mapToEntityMap(Map<String, StsKeyEntry> keyEntries) {
        return keyEntries.values().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toMap(KeyEntryAttributesEntity::getAlias, Function.identity()));
    }

    private KeyEntryAttributesEntity mapToEntity(StsKeyEntry keyEntry) {
        KeyEntryAttributesEntity entryAttributes = new KeyEntryAttributesEntity();

        entryAttributes.setAlias(keyEntry.getAlias());
        entryAttributes.setCreatedAt(convert(keyEntry.getCreatedAt()));
        entryAttributes.setNotBefore(convert(keyEntry.getNotBefore()));
        entryAttributes.setNotAfter(convert(keyEntry.getNotAfter()));
        entryAttributes.setExpireAt(convert(keyEntry.getExpireAt()));
        entryAttributes.setValidityInterval(keyEntry.getValidityInterval());
        entryAttributes.setLegacyInterval(keyEntry.getLegacyInterval());
        entryAttributes.setState(keyEntry.getState());
        entryAttributes.setKeyUsage(keyEntry.getKeyUsage());

        return entryAttributes;
    }

    private Date convert(ZonedDateTime zonedDateTime) {
        if(zonedDateTime == null) {
            return null;
        }

        return Date.from(zonedDateTime.toInstant());
    }

    private ZonedDateTime convert(Date date) {
        if(date == null) {
            return null;
        }

        return date.toInstant().atZone(ZoneOffset.UTC);
    }

    private Map<String, StsKeyEntry> mapFromEntities(java.security.KeyStore keyStore, Map<String, KeyEntryAttributesEntity> persistentKeyEntries) {
        Map<String, StsKeyEntry> mappedKeyEntries = new HashMap<>();
        Map<String, KeyEntry> keyEntries = KeyStoreService.loadEntryMap(keyStore, new KeyStoreService.SimplePasswordProvider(keyPassHandler));

        for (Map.Entry<String, KeyEntryAttributesEntity> keyEntryAttributesMapEntry : persistentKeyEntries.entrySet()) {
            KeyEntry keyEntry = keyEntries.get(keyEntryAttributesMapEntry.getKey());

            StsKeyEntry mappedKeyEntry = mapFromEntity(keyEntry, keyEntryAttributesMapEntry.getValue());
            mappedKeyEntries.put(mappedKeyEntry.getAlias(), mappedKeyEntry);
        }

        return mappedKeyEntries;
    }

    private StsKeyEntry mapFromEntity(KeyEntry keyEntry, KeyEntryAttributesEntity keyEntryAttributes) {
        return StsKeyEntry.builder()
                .alias(keyEntryAttributes.getAlias())
                .createdAt(convert(keyEntryAttributes.getCreatedAt()))
                .notBefore(convert(keyEntryAttributes.getNotBefore()))
                .notAfter(convert(keyEntryAttributes.getNotAfter()))
                .expireAt(convert(keyEntryAttributes.getExpireAt()))
                .validityInterval(keyEntryAttributes.getValidityInterval())
                .legacyInterval(keyEntryAttributes.getLegacyInterval())
                .state(keyEntryAttributes.getState())
                .keyUsage(keyEntryAttributes.getKeyUsage())

                .keyEntry(keyEntry)

                .build();
    }

    public StsKeyStore mapFromEntity(KeyStoreEntity persistentKeyStore) {
        java.security.KeyStore keyStore = KeyStoreService.loadKeyStore(persistentKeyStore.getKeystore(), keystoreName, new KeyStoreType(persistentKeyStore.getType()), keyPassHandler);

        Map<String, StsKeyEntry> mappedKeyEntries = mapFromEntities(keyStore, persistentKeyStore.getEntries());
        Date lastUpdate = persistentKeyStore.getLastUpdate();

        return StsKeyStore.builder()
                .keyStore(keyStore)
                .keyEntries(mappedKeyEntries)
                .lastUpdate(mapLastUpdate(lastUpdate))
                .build();
    }

    public ZonedDateTime mapLastUpdate(KeyStoreEntity keyStoreEntityWithLastUpdate) {
        Date lastUpdate = keyStoreEntityWithLastUpdate.getLastUpdate();
        return mapLastUpdate(lastUpdate);
    }

    private ZonedDateTime mapLastUpdate(Date lastUpdateAsDate) {
        if(lastUpdateAsDate == null) {
            return DEFAULT_LAST_UPDATE;
        }

        return convert(lastUpdateAsDate);
    }
}
