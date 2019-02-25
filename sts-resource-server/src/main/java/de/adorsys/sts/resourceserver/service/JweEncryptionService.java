package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.*;
import de.adorsys.sts.cryptoutils.JWEEncryptedSelector;
import de.adorsys.sts.resourceserver.exception.NoJwkFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JweEncryptionService implements EncryptionService {
    private static final JWKSelector encKeySelector = new JWKSelector(new JWKMatcher.Builder().keyUse(KeyUse.ENCRYPTION).build());

    private final KeyRetrieverService keyRetrieverService;

    public JweEncryptionService(KeyRetrieverService keyRetrieverService) {
        this.keyRetrieverService = keyRetrieverService;
    }

    public String encryptFor(String audience, String secret) {
        JWKSet keySetForAudience = keyRetrieverService.retrieve(audience);
        JWK selectedKey = selectKeyFrom(keySetForAudience);

        return encrypt(selectedKey, secret);
    }

    public Map<String, String> encryptFor(Iterable<String> audiences, String secret) {
        Map<String, String> encryptedSecrets = new HashMap<>();

        for(String audience : audiences) {
            String encrypted = encryptFor(audience, secret);
            encryptedSecrets.put(audience, encrypted);
        }

        return encryptedSecrets;
    }

    private JWK selectKeyFrom(JWKSet keyset) throws NoJwkFoundException {
        List<JWK> keys = encKeySelector.select(keyset);
        Collections.shuffle(keys);

        return keys.stream()
                .findAny()
                .orElseThrow(() -> new NoJwkFoundException("Cannot find a JWK for encryption"));
    }

    public String encrypt(JWK jwk, String rawSecret) throws SecretEncryptionException {
        JWEEncrypter jweEncrypter;

        jweEncrypter = JWEEncryptedSelector.getEncrypter(jwk, null, null);
        Payload payload = new Payload(rawSecret);
        // JWE encrypt secret.
        JWEObject jweObj;
        try {
            jweObj = new JWEObject(getHeader(jwk), payload);
            jweObj.encrypt(jweEncrypter);
        } catch (JOSEException e) {
            throw new SecretEncryptionException(e);
        }

        return jweObj.serialize();
    }


    private JWEHeader getHeader(JWK jwk) throws JOSEException {
        JWEHeader header;

        if (jwk instanceof RSAKey) {
            header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);
        } else if (jwk instanceof ECKey) {
            header = new JWEHeader(JWEAlgorithm.ECDH_ES_A128KW, EncryptionMethod.A192GCM);
        } else {
            return null;
        }

        return new JWEHeader.Builder(header).keyID(jwk.getKeyID()).build();
    }
}
