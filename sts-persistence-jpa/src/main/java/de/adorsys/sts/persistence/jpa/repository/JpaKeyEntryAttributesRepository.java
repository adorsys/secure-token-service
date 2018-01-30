package de.adorsys.sts.persistence.jpa.repository;

import de.adorsys.sts.persistence.jpa.entity.JpaKeyEntryAttributes;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaKeyEntryAttributesRepository extends CrudRepository<JpaKeyEntryAttributes, Long> {

    List<JpaKeyEntryAttributes> findAllByKeyStoreId(Long keyStoreId);

    JpaKeyEntryAttributes findByAlias(String alias);
}
