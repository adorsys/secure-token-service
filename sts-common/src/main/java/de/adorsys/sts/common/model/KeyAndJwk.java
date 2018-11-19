package de.adorsys.sts.common.model;

import com.nimbusds.jose.jwk.JWK;

import java.security.Key;

public class KeyAndJwk {
    public final Key key;
    public final JWK jwk;
    public KeyAndJwk(Key key, JWK jwk) {
        this.key = key;
        this.jwk = jwk;
    }
}
