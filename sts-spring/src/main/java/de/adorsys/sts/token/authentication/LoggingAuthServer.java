package de.adorsys.sts.token.authentication;

import com.nimbusds.jose.jwk.JWK;
import de.adorsys.sts.tokenauth.AuthServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LoggingAuthServer extends AuthServer {

    public LoggingAuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds, String keyCloakUrl) {
        super(name, issUrl, jwksUrl, refreshIntervalSeconds, keyCloakUrl);
    }

    @Override
    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
        super.onJsonWebKeySetRetrieved(jwks);

        if(log.isDebugEnabled()) {
            log.debug("Retrieved remote JWKS: {}", jwks);
        }
    }
}
