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
import java.util.Date;
import java.util.List;

public class AuthServer {
    private String name;
    private String issUrl;
    private String jwksUrl;
    private int refreshIntervalSeconds = 600;

    private Date refreshExp = null;
    private JWKSource<SecurityContext> jwkSource = null;

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

    public Key getJWK(String keyID) {
        Date now = new Date();
        if (refreshExp == null || now.after(refreshExp)) {
            // Refresh
            refreshExp = DateUtils.addSeconds(now, refreshIntervalSeconds);
            try {
                jwkSource = new RemoteJWKSet<>(new URL(this.jwksUrl));
            } catch (MalformedURLException e) {
                // Log exception
                return null;
            }
        }
        JWKSelector jwkSelector = new JWKSelector(new JWKMatcher.Builder().keyID(keyID).build());
        SecurityContext context = null;
        List<JWK> list;
        try {
            list = jwkSource.get(jwkSelector, context);
        } catch (KeySourceException e) {
            // Log key source exception
            return null;
        }
        if (list.isEmpty()) return null;
        JWK jwk = list.iterator().next();
        if (jwk instanceof RSAKey) {
            try {
                return ((RSAKey) jwk).toPublicKey();
            } catch (JOSEException e) {
                // Log key source exception
                return null;
            }
        } else if (jwk instanceof SecretJWK) {
            return ((SecretJWK) jwk).toSecretKey();
        } else {
            // log unknown key type
            return null;
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
}
