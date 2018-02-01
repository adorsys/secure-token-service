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
import java.util.Date;

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

    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notBefore;

    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date notAfter;

    @Column(columnDefinition= "TIMESTAMP WITH TIME ZONE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireAt;

    private Long validityInterval;
    private Long legacyInterval;

    @Enumerated(EnumType.STRING)
    private StsKeyEntry.State state;

    @Enumerated(EnumType.STRING)
    private KeyUsage keyUsage;
}
