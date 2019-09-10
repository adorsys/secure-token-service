package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.apache.commons.lang3.time.DateUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.time.Clock;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class AuthServer {
    private String name;
    private String issUrl;
    private String jwksUrl;
    private int refreshIntervalSeconds = 600;
    private Clock clock;

    private Date refreshExp = null;
    private JWKSource<SecurityContext> jwkSource = null;

    public AuthServer(String name, String issUrl, String jwksUrl, Clock clock) {
        super();
        this.name = name;
        this.issUrl = issUrl;
        this.jwksUrl = jwksUrl;
        this.clock = clock;
    }

    public AuthServer(String name, String issUrl, String jwksUrl, int refreshIntervalSeconds, Clock clock) {
        super();
        this.name = name;
        this.issUrl = issUrl;
        this.jwksUrl = jwksUrl;
        this.refreshIntervalSeconds = refreshIntervalSeconds;
        this.clock = clock;
    }

    public Key getJWK(String keyID) throws JsonWebKeyRetrievalException {
        Date now = new Date(clock.instant().toEpochMilli());
        if (refreshExp == null || now.after(refreshExp)) {
            refreshExp = DateUtils.addSeconds(now, refreshIntervalSeconds);

            try {
                jwkSource = new RemoteJWKSet<>(new URL(this.jwksUrl));
            } catch (MalformedURLException e) {
                throw new JsonWebKeyRetrievalException(e);
            }
        }
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().keyID(keyID).build());

        List<JWK> list = getJWKList(jwkSelector);

        if (list.isEmpty()) throw new JsonWebKeyRetrievalException("Unable to retrieve keys: received JWKSet is empty");

        JWK jwk = list.iterator().next();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIssUrl() {
        return issUrl;
    }

    public void setIssUrl(String issUrl) {
        this.issUrl = issUrl;
    }

    public String getJwksUrl() {
        return jwksUrl;
    }

    public void setJwksUrl(String jwksUrl) {
        this.jwksUrl = jwksUrl;
    }

    public int getRefreshIntervalSeconds() {
        return refreshIntervalSeconds;
    }

    public void setRefreshIntervalSeconds(int refreshIntervalSeconds) {
        this.refreshIntervalSeconds = refreshIntervalSeconds;
    }

    protected void onJsonWebKeySetRetrieved(List<JWK> jwks) {
    }

    public class JsonWebKeyRetrievalException extends RuntimeException {
        public JsonWebKeyRetrievalException(Throwable cause) {
            super(cause);
        }

        public JsonWebKeyRetrievalException(String message) {
            super(message);
        }
    }

    private List<JWK> getJWKList(JWKSelector jwkSelector) {
        if ("test".equals(name)) {
            JWK jwk = new OctetSequenceKey.Builder("12345678901234567890123456789012".getBytes()).build();
            return Collections.singletonList(jwk);
        }

        try {
            List<JWK> list = jwkSource.get(jwkSelector, null);
            onJsonWebKeySetRetrieved(list);
            return list;
        } catch (KeySourceException e) {
            throw new JsonWebKeyRetrievalException(e);
        }
    }
}
