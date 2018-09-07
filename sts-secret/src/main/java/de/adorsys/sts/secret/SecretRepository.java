package de.adorsys.sts.secret;

import java.util.Optional;

public interface SecretRepository {

    Secret get(String subject);
    Optional<Secret> tryToGet(String subject);
    void save(String subject, Secret secret);
}
