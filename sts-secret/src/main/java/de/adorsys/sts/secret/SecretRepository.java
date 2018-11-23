package de.adorsys.sts.secret;

import java.util.Optional;

public interface SecretRepository {

    Secret get(String subject) throws SecretEncryptionException;
    Optional<Secret> tryToGet(String subject) throws SecretReadException;
    void save(String subject, Secret secret);
}
