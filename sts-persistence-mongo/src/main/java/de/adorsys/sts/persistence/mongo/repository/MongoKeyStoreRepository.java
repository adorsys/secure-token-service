package de.adorsys.sts.persistence.mongo.repository;

import de.adorsys.sts.persistence.mongo.entity.KeyStoreEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MongoKeyStoreRepository extends MongoRepository<KeyStoreEntity, String> {

    Optional<KeyStoreEntity> findByNameAndLastChangeDateGreaterThan(String name, LocalDateTime date);

    KeyStoreEntity findByName(String name);

    long countByName(String name);
}
