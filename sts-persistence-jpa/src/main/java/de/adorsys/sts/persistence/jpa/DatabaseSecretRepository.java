package de.adorsys.sts.persistence.jpa;

import de.adorsys.sts.persistence.jpa.entity.JpaSecret;
import de.adorsys.sts.persistence.jpa.repository.JpaSecretRepository;
import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class DatabaseSecretRepository implements SecretRepository {
    private final JpaSecretRepository jpaSecretRepository;

    @Autowired
    private ApplicationContext applicationContext;

    public DatabaseSecretRepository(JpaSecretRepository jpaSecretRepository) {
        this.jpaSecretRepository = jpaSecretRepository;
    }

    @Override
    public Secret get(String subject) {
        JpaSecret foundSecret = jpaSecretRepository.findJpaSecretBySubject(subject);
        return mapFromEntity(foundSecret);
    }

    @Override
    public Optional<Secret> tryToGet(String subject) {
        JpaSecret foundSecret = jpaSecretRepository.findJpaSecretBySubject(subject);
        return Optional.ofNullable(foundSecret).map(this::mapFromEntity);
    }

    @Override
    public void save(String subject, Secret secret) {
        JpaSecret foundSecret = jpaSecretRepository.findJpaSecretBySubject(subject);

        JpaSecret secretToSave;
        if(foundSecret == null) {
            secretToSave = mapToEntity(subject, secret);
        } else {
            secretToSave = foundSecret;
            mapIntoEntity(secret, secretToSave);
        }

        jpaSecretRepository.save(secretToSave);
    }


    private Secret mapFromEntity(JpaSecret jpaSecret) {
        String value = jpaSecret.getValue();
        return new Secret(value);
    }

    private JpaSecret mapToEntity(String subject, Secret secret) {
        JpaSecret jpaSecret = new JpaSecret();

        jpaSecret.setSubject(subject);
        mapIntoEntity(secret, jpaSecret);

        return jpaSecret;
    }

    private void mapIntoEntity(Secret secret, JpaSecret jpaSecret) {
        jpaSecret.setValue(secret.getValue());
    }
}
