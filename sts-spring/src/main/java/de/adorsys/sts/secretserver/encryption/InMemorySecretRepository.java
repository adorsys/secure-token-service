package de.adorsys.sts.secretserver.encryption;

import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretEncryptionException;
import de.adorsys.sts.secret.SecretReadException;
import de.adorsys.sts.secret.SecretRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemorySecretRepository implements SecretRepository {
    private final Map<String, Secret> secrets = new HashMap<>();

    @Override
    public Secret get(String subject) throws SecretEncryptionException {
        return secrets.get(subject);
    }

    @Override
    public Optional<Secret> tryToGet(String subject) throws SecretReadException {
        Secret maybeSecret = secrets.get(subject);
        return Optional.ofNullable(maybeSecret);
    }

    @Override
    public void save(String subject, Secret secret) {
        secrets.put(subject, secret);
    }
}
