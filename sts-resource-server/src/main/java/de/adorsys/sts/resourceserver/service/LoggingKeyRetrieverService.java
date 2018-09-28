package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWKSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingKeyRetrieverService implements KeyRetrieverService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingKeyRetrieverService.class);

    private final KeyRetrieverService decoratedKeyRetrieverService;

    public LoggingKeyRetrieverService(
            KeyRetrieverService keyRetrieverService
    ) {
        this.decoratedKeyRetrieverService = keyRetrieverService;
    }

    @Override
    public JWKSet retrieve(String audience) {
        if(logger.isTraceEnabled()) logger.trace("retrieve started...");

        JWKSet retrieveJwkSet = decoratedKeyRetrieverService.retrieve(audience);

        if(logger.isTraceEnabled()) logger.trace("retrieve finished.");

        return retrieveJwkSet;
    }
}
