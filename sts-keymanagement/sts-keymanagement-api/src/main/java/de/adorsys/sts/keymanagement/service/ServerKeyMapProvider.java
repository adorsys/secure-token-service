package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.model.KeyAndJwk;
import de.adorsys.sts.cryptoutils.ServerKeyMap;
import de.adorsys.sts.cryptoutils.ServerKeysHolder;

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
