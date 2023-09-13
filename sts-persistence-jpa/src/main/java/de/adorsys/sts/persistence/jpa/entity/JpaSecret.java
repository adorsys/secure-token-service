package de.adorsys.sts.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "secret")
public class JpaSecret {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String subject;

    private String value;
}
