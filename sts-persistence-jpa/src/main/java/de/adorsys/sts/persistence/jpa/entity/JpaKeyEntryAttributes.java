package de.adorsys.sts.persistence.jpa.entity;

import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.persistence.jpa.mapping.ZonedDateTimeConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "key_entry")
public class JpaKeyEntryAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long keyStoreId;

    private String alias;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime createdAt;
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime notBefore;
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime notAfter;
    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime expireAt;

    private Long validityInterval;
    private Long legacyInterval;

    @Enumerated(EnumType.STRING)
    private StsKeyEntry.State state;

    @Enumerated(EnumType.STRING)
    private KeyUsage keyUsage;
}
