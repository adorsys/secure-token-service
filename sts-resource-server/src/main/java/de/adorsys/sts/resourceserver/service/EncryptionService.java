package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.jwk.JWK;

import java.util.Map;

public interface EncryptionService {

    String encryptFor(String audience, String secret);
    Map<String, String> encryptFor(Iterable<String> audiences, String secret);
    String encrypt(JWK jwk, String rawSecret) throws SecretEncryptionException;

}
