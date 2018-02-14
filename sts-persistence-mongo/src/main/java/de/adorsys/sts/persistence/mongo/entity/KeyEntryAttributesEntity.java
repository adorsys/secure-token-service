package de.adorsys.sts.persistence.mongo.entity;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class KeyEntryAttributesEntity {

    private String alias;

    private Date createdAt;
    private Date notBefore;
    private Date notAfter;
    private Date expireAt;

    private Long validityInterval;
    private Long legacyInterval;

    private StsKeyEntry.State state;

    private KeyUsage keyUsage;
}
