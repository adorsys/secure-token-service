package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.adorsys.jkeygen.keystore.KeyEntry;

import java.time.ZonedDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class StsKeyEntry {

    private final String alias;

    private final ZonedDateTime createdAt;

    private final Long validityInterval;

    private final Long legacyInterval;

    private final KeyUsage keyUsage;

    private final KeyEntry keyEntry;
}
