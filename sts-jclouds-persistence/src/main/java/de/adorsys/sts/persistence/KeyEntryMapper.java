package de.adorsys.sts.persistence;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.jkeygen.keystore.KeyEntry;

import java.io.IOException;
import java.time.ZonedDateTime;

public class KeyEntryMapper {

    private final ObjectMapperSPI objectMapper;

    public KeyEntryMapper(ObjectMapperSPI objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String extractEntryAttributesToString(StsKeyEntry keyEntry) {
        String valuesAsString;
        try {
            KeyEntryAttributes attributes = KeyEntryAttributes.builder()
                    .createdAt(keyEntry.getCreatedAt())
                    .validityInterval(keyEntry.getValidityInterval())
                    .legacyInterval(keyEntry.getLegacyInterval())
                    .keyUsage(keyEntry.getKeyUsage())
                    .build();

            valuesAsString = objectMapper.writeValueAsString(attributes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return valuesAsString;
    }

    public StsKeyEntry mapFromKeyEntryWithAttributes(KeyEntry keyEntry, String attributesAsString) {
        KeyEntryAttributes attributes;

        try {
            attributes = objectMapper.readValue(attributesAsString, KeyEntryAttributes.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return StsKeyEntry.builder()
                .alias(keyEntry.getAlias())
                .createdAt(attributes.getCreatedAt())
                .keyEntry(keyEntry)
                .validityInterval(attributes.getValidityInterval())
                .legacyInterval(attributes.getLegacyInterval())
                .keyUsage(attributes.getKeyUsage())
                .build();
    }

    @Getter
    @Setter
    @Builder
    private static class KeyEntryAttributes {
        private ZonedDateTime createdAt;
        private Long validityInterval;
        private Long legacyInterval;
        private KeyUsage keyUsage;
    }
}
