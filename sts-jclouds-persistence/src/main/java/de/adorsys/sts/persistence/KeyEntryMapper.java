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
                    .notBefore(keyEntry.getNotBefore())
                    .notAfter(keyEntry.getNotAfter())
                    .expireAt(keyEntry.getExpireAt())
                    .validityInterval(keyEntry.getValidityInterval())
                    .legacyInterval(keyEntry.getLegacyInterval())
                    .state(keyEntry.getState())
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
                .notBefore(attributes.getNotBefore())
                .notAfter(attributes.getNotAfter())
                .expireAt(attributes.getExpireAt())
                .validityInterval(attributes.getValidityInterval())
                .legacyInterval(attributes.getValidityInterval())
                .state(attributes.getState())
                .keyUsage(attributes.getKeyUsage())
                .keyEntry(keyEntry)
                .build();
    }

    @Getter
    @Setter
    @Builder
    private static class KeyEntryAttributes {
        private ZonedDateTime createdAt;
        private ZonedDateTime notBefore;
        private ZonedDateTime notAfter;
        private ZonedDateTime expireAt;

        private Long validityInterval;
        private Long legacyInterval;

        private StsKeyEntry.State state;
        private KeyUsage keyUsage;
    }
}
