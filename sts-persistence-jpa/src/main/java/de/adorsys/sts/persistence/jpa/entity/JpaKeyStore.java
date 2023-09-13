package de.adorsys.sts.persistence.jpa.entity;

import de.adorsys.sts.persistence.jpa.mapping.ZonedDateTimeConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "key_store")
public class JpaKeyStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type;

    @Column(length = 1024 * 1024)
    private byte[] keystore;

    @Convert(converter = ZonedDateTimeConverter.class)
    private ZonedDateTime lastUpdate;
}

