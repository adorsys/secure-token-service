package de.adorsys.sts.persistence.mongo.repository;

import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoKeyStoreRepository extends MongoRepository<KeyStoreEntity, String> {

    KeyStoreEntity findByName(String name);

    long countByName(String name);
}
