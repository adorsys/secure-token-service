package de.adorsys.sts.cryptoutils;

import com.nimbusds.jose.jwk.JWKSet;

public class ServerKeysHolder {

	private final JWKSet privateKeySet;

    private final JWKSet publicKeySet;

	public ServerKeysHolder(JWKSet privateKeySet, JWKSet publicKeySet) {
		super();
		this.privateKeySet = privateKeySet;
		this.publicKeySet = publicKeySet;
	}

	public JWKSet getPrivateKeySet() {
		return privateKeySet;
	}

	public JWKSet getPublicKeySet() {
		return publicKeySet;
	}
}
