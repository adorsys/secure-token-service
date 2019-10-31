package de.adorsys.sts.keymanagement.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Builder
@EqualsAndHashCode
public class StsKeyEntryImpl implements StsKeyEntry {

    private final String alias;

    private final ZonedDateTime createdAt;

    private final ZonedDateTime notBefore;

    private ZonedDateTime notAfter;

    private ZonedDateTime expireAt;

    private final Long validityInterval;

    private final Long legacyInterval;

    private KeyState state;

    private final KeyUsage keyUsage;

    @Override
    public void setState(KeyState state) {
        this.state = state;
    }

    @Override
    public void setNotAfter(ZonedDateTime notAfter) {
        this.notAfter = notAfter;
    }

    @Override
    public void setExpireAt(ZonedDateTime expireAt) {
        this.expireAt = expireAt;
    }
}
