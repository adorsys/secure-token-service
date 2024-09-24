package de.adorsys.sts.secret;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoggingSecretServerClient implements SecretServerClient {
    private static final Logger logger = LoggerFactory.getLogger(LoggingSecretServerClient.class);

    private final SecretServerClient decoratedSecretServerClient;

    public LoggingSecretServerClient(SecretServerClient secretServerClient) {
        this.decoratedSecretServerClient = secretServerClient;
    }

    @Override
    public String getSecret(String token, Map<String, String> additionalHeaders) {
        if(logger.isTraceEnabled()) logger.trace("get secret for token start...");

        String secret = decoratedSecretServerClient.getSecret(token, additionalHeaders);

        if(logger.isTraceEnabled()) logger.trace("get secret for token finished.");

        return secret;
    }

}
