package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaKeyStore;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaKeyStoreRepository extends CrudRepository<JpaKeyStore, Long> {

    JpaKeyStore findByName(String name);

    long countByName(String name);
}
