package de.adorsys.sts.persistence.jpa.entity;

import de.adorsys.sts.persistence.jpa.mapping.ZonedDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "key_store")
public class JpaKeyStore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String type;

    @Column(length = 1024 * 1024)
    private byte[] keystore;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime lastUpdate;
}

