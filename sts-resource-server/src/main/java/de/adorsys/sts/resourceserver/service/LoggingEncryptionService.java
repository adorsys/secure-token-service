package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWK;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LoggingEncryptionService implements EncryptionService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingEncryptionService.class);

    private final EncryptionService decoratedEncryptionService;

    public LoggingEncryptionService(EncryptionService encryptionService) {
        this.decoratedEncryptionService = encryptionService;
    }

    @Override
    public String encryptFor(String audience, String secret) {
        if(logger.isTraceEnabled()) logger.trace("encryptFor audience started...");

        String encryptedSecret = decoratedEncryptionService.encryptFor(audience, secret);

        if(logger.isTraceEnabled()) logger.trace("encryptFor audience finished.");

        return encryptedSecret;
    }

    @Override
    public Map<String, String> encryptFor(Iterable<String> audiences, String secret) {
        if(logger.isTraceEnabled()) logger.trace("encryptFor audiences started...");

        Map<String, String> encryptedSecrets = decoratedEncryptionService.encryptFor(audiences, secret);

        if(logger.isTraceEnabled()) logger.trace("encryptFor audiences finished.");

        return encryptedSecrets;
    }

    @Override
    public String encrypt(JWK jwk, String rawSecret) throws SecretEncryptionException {
        if(logger.isTraceEnabled()) logger.trace("encrypt started...");

        String encryptedSecret = decoratedEncryptionService.encrypt(jwk, rawSecret);

        if(logger.isTraceEnabled()) logger.trace("encrypt finished.");

        return encryptedSecret;
    }
}
