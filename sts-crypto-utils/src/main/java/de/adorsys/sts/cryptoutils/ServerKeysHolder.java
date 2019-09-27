package de.adorsys.sts.cryptoutils;

import com.nimbusds.jose.jwk.JWKSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServerKeysHolder {

	@NonNull
	private final JWKSet privateKeySet;

	@NonNull
    private final JWKSet publicKeySet;
}
