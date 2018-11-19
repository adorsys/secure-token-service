package de.adorsys.sts.keymanagement.model;

import de.adorsys.sts.cryptoutils.KeyEntry;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class StsKeyEntry {

    private final String alias;

    private final ZonedDateTime createdAt;

    private final ZonedDateTime notBefore;

    private ZonedDateTime notAfter;

    private ZonedDateTime expireAt;

    private final Long validityInterval;

    private final Long legacyInterval;

    private State state;

    private final KeyUsage keyUsage;

    private final KeyEntry keyEntry;

    public void setState(State state) {
        this.state = state;
    }

    public void setNotAfter(ZonedDateTime notAfter) {
        this.notAfter = notAfter;
    }

    public void setExpireAt(ZonedDateTime expireAt) {
        this.expireAt = expireAt;
    }

    public enum State {
        CREATED,
        VALID,
        LEGACY,
        EXPIRED
    }
}
