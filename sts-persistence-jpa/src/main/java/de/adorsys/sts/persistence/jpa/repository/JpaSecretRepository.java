package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSecretRepository extends JpaRepository<JpaSecret, Long> {

    JpaSecret findJpaSecretBySubject(String subject);
}
