package de.adorsys.sts.persistence.mongo.repository;

import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MongoKeyStoreRepository extends MongoRepository<KeyStoreEntity, String> {

    KeyStoreEntity findByName(String name);

    long countByName(String name);

    @Query(value = "{ 'name' : ?0 }", fields = "{ lastUpdate : 1 }")
    List<KeyStoreEntity> findLastUpdate(String name);
}
