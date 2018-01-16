package de.adorsys.sts.persistence;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.jkeygen.keystore.KeyEntry;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

public class KeyEntryMapper {

    private static final String CREATED_AT_ATTRIBUTE_KEY = "createdAt";
    private static final String VALIDITY_ATTRIBUTE_KEY = "validity";
    private static final String LEGACY_ATTRIBUTE_KEY = "legacy";
    private static final String USAGE_ATTRIBUTE_KEY = "usage";

    private final ObjectMapperSPI objectMapper;

    public KeyEntryMapper(ObjectMapperSPI objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String extractEntryAttributesToString(StsKeyEntry keyEntry) {
        Map<String, Object> keyEntryValues = new HashMap<>();

        keyEntryValues.put(CREATED_AT_ATTRIBUTE_KEY, keyEntry.getCreatedAt());
        keyEntryValues.put(VALIDITY_ATTRIBUTE_KEY, keyEntry.getValidityInterval());
        keyEntryValues.put(LEGACY_ATTRIBUTE_KEY, keyEntry.getLegacyInterval());
        keyEntryValues.put(USAGE_ATTRIBUTE_KEY, keyEntry.getKeyUsage());

        String valuesAsString;
        try {
            valuesAsString = objectMapper.writeValueAsString(keyEntryValues);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return valuesAsString;
    }

    public StsKeyEntry mapFromKeyEntryWithAttributes(KeyEntry keyEntry, String attributesAsString) {
        Map<String, String> attributesAsMap;

        try {
            attributesAsMap = objectMapper.readValue(attributesAsString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ZonedDateTime createdAt = ZonedDateTime.parse(attributesAsMap.get(CREATED_AT_ATTRIBUTE_KEY));
        Long validityInterval = Long.parseLong(attributesAsMap.get(VALIDITY_ATTRIBUTE_KEY));
        Long legacyInterval = Long.parseLong(attributesAsMap.get(LEGACY_ATTRIBUTE_KEY));
        KeyUsage keyUsage = KeyUsage.valueOf(attributesAsMap.get(USAGE_ATTRIBUTE_KEY));

        return StsKeyEntry.builder()
                .alias(keyEntry.getAlias())
                .createdAt(createdAt)
                .keyEntry(keyEntry)
                .validityInterval(validityInterval)
                .legacyInterval(legacyInterval)
                .keyUsage(keyUsage)
                .build();
    }
}
