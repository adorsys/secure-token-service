package de.adorsys.sts.simpleencryption.keyprovider;

import com.nimbusds.jose.jwk.JWK;
import de.adorsys.sts.simpleencryption.KeyProvider;
import de.adorsys.sts.common.converter.KeyConverter;

import java.security.Key;
import java.text.ParseException;

class StaticAesKeyProvider implements KeyProvider {
    private final String key;

    StaticAesKeyProvider(String key) {
        this.key = key;
    }

    @Override
    public Key getKeyForEncryption() {
        return extractSecretKey(key);
    }

    @Override
    public Key getKeyForDecryption(String keyId) {
        return extractSecretKey(key);
    }

    private static Key extractSecretKey(String jwkAsString) {
        Key key;

        try {
            JWK parsedKey = JWK.parse(jwkAsString);
            key = KeyConverter.toPrivateOrSecret(parsedKey, "AES");
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return key;
    }
}
