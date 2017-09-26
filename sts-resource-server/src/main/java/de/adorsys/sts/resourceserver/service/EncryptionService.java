package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.*;
import org.adorsys.jjwk.selector.JWEEncryptedSelector;
import org.adorsys.jjwk.selector.KeyExtractionException;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EncryptionService {

    private static final JWKSelector encKeySelector = new JWKSelector(new JWKMatcher.Builder().keyUse(KeyUse.ENCRYPTION).build());

    private final KeyRetrieverService keyRetrieverService;

    @Autowired
    public EncryptionService(KeyRetrieverService keyRetrieverService) {
        this.keyRetrieverService = keyRetrieverService;
    }

    public String encryptFor(String clientId, String secret) {
        JWKSet keySetForClient = keyRetrieverService.retrieve(clientId);

        JWK selectedKey = selectKeyFrom(keySetForClient);

        return encrypt(selectedKey, secret);
    }

    private JWK selectKeyFrom(JWKSet keyset) {
        List<JWK> keys = encKeySelector.select(keyset);

        return keys.stream().filter(
                k -> KeyUse.ENCRYPTION == k.getKeyUse()
        ).findFirst().orElseThrow(RuntimeException::new);
    }

    public String encrypt(JWK jwk, String rawSecret) throws SecretEncryptionException {
        JWEEncrypter jweEncrypter;

        try {
            jweEncrypter = JWEEncryptedSelector.geEncrypter(jwk, null, null);
        } catch (UnsupportedEncAlgorithmException | KeyExtractionException | UnsupportedKeyLengthException e) {
            throw new SecretEncryptionException(e);
        }
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
