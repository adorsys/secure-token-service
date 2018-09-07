package de.adorsys.sts.persistence.mongo.repository;

import de.adorsys.sts.persistence.mongo.entity.SecretEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoSecretRepository extends MongoRepository<SecretEntity, String> {

    SecretEntity findBySubject(String subject);
}
