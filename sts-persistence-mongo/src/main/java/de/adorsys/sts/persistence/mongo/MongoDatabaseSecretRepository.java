package de.adorsys.sts.persistence.mongo;

import de.adorsys.sts.persistence.mongo.entity.SecretEntity;
import de.adorsys.sts.persistence.mongo.repository.MongoSecretRepository;
import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MongoDatabaseSecretRepository implements SecretRepository {

    private final MongoSecretRepository secretRepository;

    @Autowired
    public MongoDatabaseSecretRepository(MongoSecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    @Override
    public Secret get(String subject) {
        return findSecretBySubject(subject);
    }

    @Override
    public Optional<Secret> tryToGet(String subject) {
        return Optional.ofNullable(findSecretBySubject(subject));
    }

    @Override
    public void save(String subject, Secret secret) {
        SecretEntity secretEntity = new SecretEntity();

        secretEntity.setSubject(subject);
        secretEntity.setValue(secret.getValue());

        secretRepository.save(secretEntity);
    }

    private Secret findSecretBySubject(String subject) {
        SecretEntity foundSecret = secretRepository.findBySubject(subject);

        if(foundSecret == null) {
            return null;
        }

        return new Secret(foundSecret.getValue());
    }
}
