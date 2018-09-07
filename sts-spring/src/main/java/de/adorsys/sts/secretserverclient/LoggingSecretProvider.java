package de.adorsys.sts.secretserverclient;

import de.adorsys.sts.keymanagement.service.SecretProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSecretProvider implements SecretProvider {
    private static final Logger logger = LoggerFactory.getLogger(LoggingSecretProvider.class);

    private final SecretProvider decoratedSecretProvider;

    public LoggingSecretProvider(SecretProvider secretProvider) {
        this.decoratedSecretProvider = secretProvider;
    }

    @Override
    public String get() {
        if(logger.isTraceEnabled()) logger.trace("get secret start...");

        String secret = decoratedSecretProvider.get();

        if(logger.isTraceEnabled()) logger.trace("get secret finished.");

        return secret;
    }
}
