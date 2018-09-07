package de.adorsys.sts.persistence.mongo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
public class SecretEntity {

    @Id
    private String id;

    @Indexed
    private String subject;

    private String value;
}
