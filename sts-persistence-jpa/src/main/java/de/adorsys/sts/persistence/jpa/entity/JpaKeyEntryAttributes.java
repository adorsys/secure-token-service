package de.adorsys.sts.persistence.jpa.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.adorsys.sts.keymanagement.model.KeyUsage;
import de.adorsys.sts.keymanagement.model.StsKeyEntry;
import de.adorsys.sts.persistence.jpa.mapping.ZonedDateTimeConverter;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.hibernate.type.ZonedDateTimeType;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;

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
