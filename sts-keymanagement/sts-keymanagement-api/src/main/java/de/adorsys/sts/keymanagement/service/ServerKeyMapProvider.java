package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.model.KeyAndJwk;

import java.security.Key;

public interface ServerKeyMapProvider {

    JWKSet getPublicKeys();

    KeyAndJwk randomSecretKey();

    KeyAndJwk randomSignKey();

    Key getKey(String keyId);
}
