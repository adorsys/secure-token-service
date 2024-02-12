package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthServer {
    @Setter
    @Getter
    private String name;
    @Getter
    private final String issUrl;
    private final String jwksUrl;
    private final int refreshIntervalSeconds;

    @Setter
    JWKSource<SecurityContext> jwkSource;

    final ConcurrentHashMap<String, JWK> jwkCache = new ConcurrentHashMap<>();
    long lastCacheUpdate = 0;

    public AuthServer(String name, String issUrl, String jwksUrl) {
        this(name, issUrl, jwksUrl, 600);
    }

    @SneakyThrows
    public AuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds) {
        super();
        this.name = name;
        this.issUrl = issUrl;
        this.jwksUrl = jwksUrl;
        this.refreshIntervalSeconds = refreshIntervalSeconds;

        jwkSource = JWKSourceBuilder.create(new URL(this.jwksUrl)).build();
    }

    private void updateJwkCache() throws JsonWebKeyRetrievalException {
        log.debug("Thread entering updateJwkCache: " + Thread.currentThread().getId());

        try {

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

        log.debug("Thread leaving updateJwkCache: " + Thread.currentThread().getId());
    }

    public Key getJWK(String keyID) throws JsonWebKeyRetrievalException {
        log.debug("Thread entering getJWK: {}", Thread.currentThread().getId());

        Date now = new Date();
        long currentTime = now.getTime();

        // Check if the cache is still valid
        if (currentTime - lastCacheUpdate > refreshIntervalSeconds * 1000L || jwkCache.isEmpty()) {
            log.debug("Cache is invalid or empty, updating the cache...");
            updateJwkCache();
            log.debug("Cache updated successfully");
        }

        JWK jwk = jwkCache.get(keyID);
        if (jwk == null) {
            log.error("Key with ID {} not found in cache", keyID);
            throw new JsonWebKeyRetrievalException("Key with ID " + keyID + " not found in cache");
        }

        log.debug("JWK for key ID {} found in cache", keyID);

        if (jwk instanceof RSAKey) {
            try {
                log.debug("JWK is instance of RSAKey");
                return ((RSAKey) jwk).toPublicKey();
            } catch (JOSEException e) {
                log.error("Error while converting RSAKey to public key", e);
                throw new JsonWebKeyRetrievalException(e);
            }
        } else if (jwk instanceof SecretJWK) {
            log.debug("JWK is instance of SecretJWK");
            return ((SecretJWK) jwk).toSecretKey();
        } else {
            log.error("Unknown key type {}", jwk.getClass());
            throw new JsonWebKeyRetrievalException("unknown key type " + jwk.getClass());
        }
    }

    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
        log.info("Retrieved {} keys from {}", jwks.size(), jwksUrl);
    }

    protected static class JsonWebKeyRetrievalException extends RuntimeException {
        public JsonWebKeyRetrievalException(Throwable cause) {
            super(cause);
        }

        public JsonWebKeyRetrievalException(String message) {
            super(message);
        }
    }
}
