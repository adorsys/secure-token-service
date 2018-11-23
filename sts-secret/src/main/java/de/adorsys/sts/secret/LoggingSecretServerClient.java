package de.adorsys.sts.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSecretServerClient implements SecretServerClient {
    private static final Logger logger = LoggerFactory.getLogger(LoggingSecretServerClient.class);

    private final SecretServerClient decoratedSecretServerClient;

    public LoggingSecretServerClient(SecretServerClient secretServerClient) {
        this.decoratedSecretServerClient = secretServerClient;
    }

    @Override
    public String getSecret(String token) {
        if(logger.isTraceEnabled()) logger.trace("get secret for token start...");

        String secret = decoratedSecretServerClient.getSecret(token);

        if(logger.isTraceEnabled()) logger.trace("get secret for token finished.");

        return secret;
    }
}
