package de.adorsys.sts.persistence.mongo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class KeyStoreEntity {

    @Id
    private String id;

    @Indexed
    private String name;

    private LocalDateTime lastChangeDate;

    private String type;

    private byte[] keystore;

    private Map<String, KeyEntryAttributesEntity> entries;
}
