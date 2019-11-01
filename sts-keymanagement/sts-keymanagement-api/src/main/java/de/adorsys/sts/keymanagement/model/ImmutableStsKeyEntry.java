package de.adorsys.sts.keymanagement.model;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
public class ImmutableStsKeyEntry implements StsKeyEntry {

    @Delegate(excludes = Exclude.class)
    private final StsKeyEntry entry;

    private interface Exclude {

        void setState(KeyState state);
        void setNotAfter(ZonedDateTime notAfter);
        void setExpireAt(ZonedDateTime expireAt);
    }

    @Override
    public void setState(KeyState state) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void setNotAfter(ZonedDateTime notAfter) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void setExpireAt(ZonedDateTime expireAt) {
        throw new IllegalStateException("Not implemented");
    }
}
