package de.adorsys.sts.tests.e2e.testcomponents;

import com.nimbusds.jose.jwk.OctetSequenceKey;
import de.adorsys.sts.token.authentication.LoggingAuthServer;

import java.security.Key;

public class AuthServerTestable extends LoggingAuthServer {

    public AuthServerTestable(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds) {
        super(name, issUrl, jwksUrl, refreshIntervalSeconds);
    }

    @Override
    public Key getJWK(String keyID) throws JsonWebKeyRetrievalException {
        OctetSequenceKey jwk = new OctetSequenceKey.Builder("12345678901234567890123456789012".getBytes()).build();
        return jwk.toSecretKey();
    }
}
