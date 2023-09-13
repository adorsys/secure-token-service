package de.adorsys.sts.token.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import de.adorsys.sts.tokenauth.AuthServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggingAuthServer extends AuthServer {
    private static final Logger LOG = LoggerFactory.getLogger(LoggingBearerTokenValidator.class);
    private final ObjectMapper objectMapper;

    public LoggingAuthServer(String name, String issUrl, String jwksUrl, ObjectMapper objectMapper) {
        super(name, issUrl, jwksUrl);
        this.objectMapper = objectMapper;
    }

    public LoggingAuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds, ObjectMapper objectMapper) {
        super(name, issUrl, jwksUrl, refreshIntervalSeconds);
        this.objectMapper = objectMapper;
    }

    @Override
    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
        super.onJsonWebKeySetRetrieved(jwks);
    }
}
