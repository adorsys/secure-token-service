package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaKeyEntryAttributes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaKeyEntryAttributesRepository extends CrudRepository<JpaKeyEntryAttributes, Long> {

    List<JpaKeyEntryAttributes> findAllByKeyStoreId(Long keyStoreId);

    JpaKeyEntryAttributes findByAlias(String alias);
}
