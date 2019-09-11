package de.adorsys.sts.tests.e2e.tokenexchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import de.adorsys.sts.token.authentication.LoggingAuthServer;

import java.security.Key;

public class AuthServerTestable extends LoggingAuthServer {

    public AuthServerTestable(String name, String issUrl, String jwksUrl, ObjectMapper objectMapper) {
        super(name, issUrl, jwksUrl, objectMapper);
    }

    @Override
    public Key getJWK(String keyID) throws JsonWebKeyRetrievalException {
        OctetSequenceKey jwk = new OctetSequenceKey.Builder("12345678901234567890123456789012".getBytes()).build();
        return jwk.toSecretKey();
    }
}
