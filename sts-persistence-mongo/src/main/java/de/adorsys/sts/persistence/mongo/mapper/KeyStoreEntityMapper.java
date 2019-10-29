package de.adorsys.sts.persistence.mongo.mapper;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.keystore.KeyStoreView;
import de.adorsys.keymanagement.api.types.entity.KeyEntry;
import de.adorsys.keymanagement.api.types.template.NameAndPassword;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyEntry;
import de.adorsys.sts.keymanagement.model.PasswordCallbackHandler;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.model.UnmodifyableKeystore;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.persistence.mongo.entity.KeyEntryAttributesEntity;
import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyStore;
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

    public KeyStoreEntity mapToEntity(StsKeyStore keyStore) {
        KeyStoreEntity persistentKeyStore = new KeyStoreEntity();

        mapIntoEntity(keyStore, persistentKeyStore);

        return persistentKeyStore;
    }

    public void mapIntoEntity(StsKeyStore keyStore, KeyStoreEntity persistentKeyStore) {
        UnmodifyableKeystore toPersist = keyStore.getKeyStoreCopy();
        byte[] bytes = toPersist.toBytes(juggler, keyPassHandler::getPassword);

        persistentKeyStore.setName(keystoreName);
        persistentKeyStore.setKeystore(bytes);
        persistentKeyStore.setType(toPersist.getType());
        persistentKeyStore.setLastUpdate(convert(keyStore.getLastUpdate()));

        Map<String, KeyEntryAttributesEntity> mappedEntryAttributes = mapToEntityMap(keyStore.getEntries());
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

    private Map<String, StsKeyEntry> mapFromEntities(Map<String, KeyEntryAttributesEntity> persistentKeyEntries) {
        Map<String, StsKeyEntry> mappedKeyEntries = new HashMap<>();
        for (Map.Entry<String, KeyEntryAttributesEntity> keyEntryAttributesMapEntry : persistentKeyEntries.entrySet()) {
            StsKeyEntry mappedKeyEntry = mapFromEntity(keyEntryAttributesMapEntry.getValue());
            mappedKeyEntries.put(mappedKeyEntry.getAlias(), mappedKeyEntry);
        }

        return mappedKeyEntries;
    }

    private StsKeyEntry mapFromEntity(KeyEntryAttributesEntity keyEntryAttributes) {
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
                .build();
    }

    public StsKeyStore mapFromEntity(KeyStoreEntity persistentKeyStore) {
        KeyStore orig = juggler.serializeDeserialize()
                .deserialize(persistentKeyStore.getKeystore(), keyPassHandler::getPassword);

        Map<String, StsKeyEntry> mappedKeyEntries = mapFromEntities(persistentKeyStore.getEntries());
        Date lastUpdate = persistentKeyStore.getLastUpdate();

        KeyStore keyStore = upgradeKeyStoreIfNeeded(orig, mappedKeyEntries);
        return StsKeyStore.builder()
                .keyStore(keyStore)
                .view(juggler.readKeys().fromKeyStore(keyStore, id -> keyPassHandler.getPassword()).entries())
                .lastUpdate(mapLastUpdate(lastUpdate))
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
