package de.adorsys.sts.simpleencryption.encrypt;

import com.nimbusds.jose.*;
import de.adorsys.sts.cryptoutils.JWEEncryptedSelector;
import de.adorsys.sts.simpleencryption.EncryptionException;
import de.adorsys.sts.simpleencryption.KeyProvider;

public class JweEncrypter implements Encrypter {

    private final JWEEncrypter jweEncrypter;
    private final JWEHeader header;

    public JweEncrypter(KeyProvider keyProvider, final JWEAlgorithm alg, final EncryptionMethod enc) {
        jweEncrypter = JWEEncryptedSelector.getEncrypter(keyProvider.getKeyForEncryption(), alg, enc);
        header = new JWEHeader(alg, enc);
    }

    public String encrypt(String plainText) {
        Payload payload = new Payload(plainText);
        JWEObject jweObject = new JWEObject(header, payload);

        try {
            jweObject.encrypt(jweEncrypter);
        } catch (JOSEException e) {
            throw new EncryptionException(e);
        }

        return jweObject.serialize();
    }
}
