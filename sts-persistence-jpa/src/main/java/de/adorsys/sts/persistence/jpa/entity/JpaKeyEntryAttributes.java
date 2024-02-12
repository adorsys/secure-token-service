package de.adorsys.sts.persistence.jpa.entity;

import de.adorsys.sts.keymanagement.model.KeyState;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.persistence.jpa.mapping.ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "key_entry")
public class JpaKeyEntryAttributes {

    @Id
    @SequenceGenerator(name = "key_entry_seq", sequenceName = "key_entry_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "key_entry_seq")
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
    private KeyState state;

    @Enumerated(EnumType.STRING)
    private KeyUsage keyUsage;
}
