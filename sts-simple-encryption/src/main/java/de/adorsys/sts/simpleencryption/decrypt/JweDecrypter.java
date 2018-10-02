package de.adorsys.sts.simpleencryption.decrypt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import de.adorsys.sts.simpleencryption.EncryptionException;
import de.adorsys.sts.simpleencryption.KeyProvider;

import java.security.Key;
import java.text.ParseException;
import java.util.Optional;

public class JweDecrypter implements Decrypter {

    private final KeyProvider keyProvider;
    private final DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();

    public JweDecrypter(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public String decrypt(String encrypted) {
        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(encrypted);
        } catch (ParseException e) {
            throw new EncryptionException(e);
        }

        String keyID = jweObject.getHeader().getKeyID();
        Key key = keyProvider.getKeyForDecryption(keyID);
        if (key == null) {
            throw new IllegalStateException("No suitable key found");
        }

        JWEDecrypter decrypter;
        try {
            decrypter = decrypterFactory.createJWEDecrypter(jweObject.getHeader(), key);
        } catch (JOSEException e) {
            throw new EncryptionException(e);
        }

        try {
            jweObject.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new EncryptionException(e);
        }

        return jweObject.getPayload().toString();
    }

    @Override
    public Optional<String> tryToDecrypt(String encrypted) {
        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(encrypted);
        } catch (ParseException e) {
            return Optional.empty();
        }

        String keyID = jweObject.getHeader().getKeyID();
        Key key = keyProvider.getKeyForDecryption(keyID);
        if (key == null) {
            return Optional.empty();
        }

        JWEDecrypter decrypter;
        try {
            decrypter = decrypterFactory.createJWEDecrypter(jweObject.getHeader(), key);
        } catch (JOSEException e) {
            return Optional.empty();
        }

        try {
            jweObject.decrypt(decrypter);
        } catch (JOSEException e) {
            return Optional.empty();
        }

        String decrypted = jweObject.getPayload().toString();
        return Optional.of(decrypted);
    }
}
