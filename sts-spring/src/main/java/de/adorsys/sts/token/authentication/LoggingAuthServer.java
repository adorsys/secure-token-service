package de.adorsys.sts.token.authentication;

import com.nimbusds.jose.jwk.JWK;
import de.adorsys.sts.tokenauth.AuthServer;

import java.util.List;

public class LoggingAuthServer extends AuthServer {

    public LoggingAuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds, String keyCloakUrl) {
        super(name, issUrl, jwksUrl, refreshIntervalSeconds, keyCloakUrl);
    }

    @Override
    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
        super.onJsonWebKeySetRetrieved(jwks);
    }
}
