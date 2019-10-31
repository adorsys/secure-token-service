package de.adorsys.sts.keymanagement.model;

import de.adorsys.keymanagement.api.types.entity.metadata.KeyMetadata;

import java.time.ZonedDateTime;

public interface StsKeyEntry extends KeyMetadata {

    void setState(KeyState state);
    void setNotAfter(ZonedDateTime notAfter);
    void setExpireAt(ZonedDateTime expireAt);

    String getAlias();
    ZonedDateTime getCreatedAt();
    ZonedDateTime getNotBefore();
    ZonedDateTime getNotAfter();
    ZonedDateTime getExpireAt();
    Long getValidityInterval();
    Long getLegacyInterval();
    KeyState getState();
    KeyUsage getKeyUsage();
}
