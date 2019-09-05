package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaKeyStore;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;

@Repository
public interface JpaKeyStoreRepository extends CrudRepository<JpaKeyStore, Long> {

    JpaKeyStore findByName(String name);

    long countByName(String name);

    @Query("select k.lastUpdate from JpaKeyStore k where k.name = :name")
    ZonedDateTime getLastUpdate(@Param("name") String name);
}
