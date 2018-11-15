package de.adorsys.sts.simpleencryption.keyprovider;

import com.nimbusds.jose.jwk.JWK;
import de.adorsys.sts.cryptoutils.KeyConverter;
import de.adorsys.sts.simpleencryption.KeyProvider;

import java.security.Key;
import java.text.ParseException;

class StaticRsaKeyProvider implements KeyProvider {
    private final String key;

    StaticRsaKeyProvider(String key) {
        this.key = key;
    }

    @Override
    public Key getKeyForEncryption() {
        JWK parsedKey = tryToParseJwk(key);
        return KeyConverter.toPublic(parsedKey);
    }

    @Override
    public Key getKeyForDecryption(String keyId) {
        JWK parsedKey = tryToParseJwk(key);
        return KeyConverter.toPrivateOrSecret(parsedKey);
    }

    private JWK tryToParseJwk(String key) {
        JWK parsedKey;

        try {
            parsedKey = JWK.parse(key);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }

        return parsedKey;
    }
}
