package de.adorsys.sts.keymanagement.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.factories.DefaultJWEDecrypterFactory;
import de.adorsys.sts.cryptoutils.ServerKeyMapProvider;
import de.adorsys.sts.keymanagement.exceptions.SecretDecryptionException;

import java.security.Key;
import java.text.ParseException;

public class DecryptionServiceImpl implements DecryptionService {

    private final ServerKeyMapProvider keyMapProvider;
    private final DefaultJWEDecrypterFactory decrypterFactory = new DefaultJWEDecrypterFactory();


    public DecryptionServiceImpl(ServerKeyMapProvider keyMapProvider) {
        this.keyMapProvider = keyMapProvider;
    }

    @Override
    public String decrypt(String encrypted) {
        JWEObject jweObject;
        try {
            jweObject = JWEObject.parse(encrypted);
        } catch (ParseException e) {
            throw new SecretDecryptionException(e);
        }

        String keyID = jweObject.getHeader().getKeyID();
        Key key = keyMapProvider.getKey(keyID);
        if (key == null) {
            throw new IllegalStateException("No suitable key found");
        }

        JWEDecrypter decrypter;
        try {
            decrypter = decrypterFactory.createJWEDecrypter(jweObject.getHeader(), key);
        } catch (JOSEException e) {
            throw new SecretDecryptionException(e);
        }

        try {
            jweObject.decrypt(decrypter);
        } catch (JOSEException e) {
            throw new SecretDecryptionException(e);
        }

        return jweObject.getPayload().toString();
    }
}
