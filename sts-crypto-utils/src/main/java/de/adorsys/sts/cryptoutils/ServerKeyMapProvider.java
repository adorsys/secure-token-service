package de.adorsys.sts.cryptoutils;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.model.KeyAndJwk;

import java.security.Key;

public interface ServerKeyMapProvider {

    @Deprecated
    ServerKeyMap getKeyMap();

    @Deprecated
    ServerKeysHolder getServerKeysHolder();

    JWKSet getPublicKeys();

    KeyAndJwk randomSecretKey();

    KeyAndJwk randomSignKey();

    Key getKey(String keyId);
}
