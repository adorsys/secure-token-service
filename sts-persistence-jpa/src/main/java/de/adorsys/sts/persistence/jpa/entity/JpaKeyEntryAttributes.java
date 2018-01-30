package de.adorsys.sts.persistence.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "key_entry", schema = "sts")
public class JpaKeyEntryAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long keyStoreId;

    private String alias;

    private ZonedDateTime createdAt;
    private ZonedDateTime notBefore;
    private ZonedDateTime notAfter;
    private ZonedDateTime expireAt;

    private Long validityInterval;
    private Long legacyInterval;

    @Enumerated(EnumType.STRING)
    private StsKeyEntry.State state;

    @Enumerated(EnumType.STRING)
    private KeyUsage keyUsage;
}
