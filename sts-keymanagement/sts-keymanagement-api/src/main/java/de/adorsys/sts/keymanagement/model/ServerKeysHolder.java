package de.adorsys.sts.keymanagement.model;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServerKeysHolder {

	private final JWKSet privateKeySet;
    private final JWKSet publicKeySet;
}
