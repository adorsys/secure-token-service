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

    private final ZonedDateTime notBefore;

    private final ZonedDateTime notAfter;

    private final ZonedDateTime expireAt;

    private State state;

    private final KeyUsage keyUsage;

    private final KeyEntry keyEntry;

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        CREATED,
        VALID,
        LEGACY,
        EXPIRED
    }
}
