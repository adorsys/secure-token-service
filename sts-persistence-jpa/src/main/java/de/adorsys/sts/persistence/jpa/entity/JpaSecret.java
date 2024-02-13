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
    @SequenceGenerator(name = "hibernate_secret_seq", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "hibernate_secret_seq")
    private Long id;

    private String subject;

    private String value;
}
