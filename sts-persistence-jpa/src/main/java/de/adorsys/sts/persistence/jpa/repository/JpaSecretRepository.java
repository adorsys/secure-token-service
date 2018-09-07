package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaSecret;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSecretRepository extends CrudRepository<JpaSecret, Long> {

    JpaSecret findBySubject(String audience);
}
