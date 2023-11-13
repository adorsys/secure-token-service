package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;

import java.net.URL;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServer {
    @Getter
    private String name;
    @Getter
    private final String issUrl;
    private final String jwksUrl;
    private int refreshIntervalSeconds = 600;

    private final ConcurrentHashMap<String, JWK> jwkCache = new ConcurrentHashMap<>();
    private long lastCacheUpdate = 0;

    public AuthServer(String name, String issUrl, String jwksUrl) {
        super();
        this.name = name;
        this.issUrl = issUrl;
        this.jwksUrl = jwksUrl;
    }

    public AuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds) {
        super();
        this.name = name;
        this.issUrl = issUrl;
        this.jwksUrl = jwksUrl;
        this.refreshIntervalSeconds = refreshIntervalSeconds;
    }

    private void updateJwkCache() throws JsonWebKeyRetrievalException {
        try {
            JWKSource<SecurityContext> jwkSource = new RemoteJWKSet<>(new URL(this.jwksUrl));
            List<JWK> jwks = jwkSource.get(new JWKSelector(new JWKMatcher.Builder().build()), null);
            onJsonWebKeySetRetrieved(jwks);

            // Update the cache
            jwkCache.clear();
            for (JWK jwk : jwks) {
                jwkCache.put(jwk.getKeyID(), jwk);
            }
            lastCacheUpdate = new Date().getTime();
        } catch (Exception e) {
            throw new JsonWebKeyRetrievalException(e);
        }
    }

    public Key getJWK(String keyID) throws JsonWebKeyRetrievalException {
        Date now = new Date();
        long currentTime = now.getTime();

        // Check if the cache is still valid
        if (currentTime - lastCacheUpdate > refreshIntervalSeconds * 1000L || jwkCache.isEmpty()) {
            updateJwkCache();
        }

        JWK jwk = jwkCache.get(keyID);
        if (jwk == null) {
            throw new JsonWebKeyRetrievalException("Key with ID " + keyID + " not found in cache");
        }

        if (jwk instanceof RSAKey) {
            try {
                return ((RSAKey) jwk).toPublicKey();
            } catch (JOSEException e) {
                throw new JsonWebKeyRetrievalException(e);
            }
        } else if (jwk instanceof SecretJWK) {
            return ((SecretJWK) jwk).toSecretKey();
        } else {
            throw new JsonWebKeyRetrievalException("unknown key type " + jwk.getClass());
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
    }

    public static class JsonWebKeyRetrievalException extends RuntimeException {
        public JsonWebKeyRetrievalException(Throwable cause) {
            super(cause);
        }

        public JsonWebKeyRetrievalException(String message) {
            super(message);
        }
    }
}
