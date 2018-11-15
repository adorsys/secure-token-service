package de.adorsys.sts.cryptoutils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.*;
import org.adorsys.cryptoutils.exceptions.BaseException;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.KeyPair;

/**
 * This class is a subset-clone of {@link org.adorsys.jjwk.serverkey.KeyConverter} to avoid hard dependencies to cryptoutils project
 */
public class KeyConverter {

    /**
     * Converts the specified of JSON Web Keys (JWK) it's standard Java
     * class representation. Asymmetric {@link RSAKey RSA} and
     * {@link ECKey EC key} pairs are converted to
     * {@link java.security.PublicKey} and {@link java.security.PrivateKey}
     * (if specified) objects. {@link OctetSequenceKey secret JWKs} are
     * converted to {@link javax.crypto.SecretKey} objects.
     *
     * @param jwk jwk
     * @return private key, secret key or nul;
     */
    public static Key toPrivateOrSecret(final JWK jwk) {
        try {
            if (jwk instanceof RSAKey) {
                KeyPair keyPair = ((RSAKey)jwk).toKeyPair();
                if (keyPair.getPrivate() != null) {
                    return keyPair.getPrivate();
                }
            } else if (jwk instanceof SecretJWK) {
                return ((SecretJWK)jwk).toSecretKey();
            }
        } catch (JOSEException e) {
            return null;
        }
        return null;
    }

    /**
     * Converts the specified of JSON Web Keys (JWK) it's standard Java
     * class representation. Asymmetric {@link RSAKey RSA} and
     * {@link ECKey EC key} pairs are converted to
     * {@link java.security.PublicKey} and {@link java.security.PrivateKey}
     * (if specified) objects. {@link OctetSequenceKey secret JWKs} are
     * converted to {@link javax.crypto.SecretKey} objects.
     *
     * @param jwk jwk
     * @return private key, secret key or nul;
     */
    public static Key toPrivateOrSecret(final JWK jwk, String alg) {
        try {
            if (jwk instanceof RSAKey) {
                KeyPair keyPair = ((RSAKey)jwk).toKeyPair();
                if (keyPair.getPrivate() != null) {
                    return keyPair.getPrivate();
                }
            } else if (jwk instanceof SecretJWK) {
                byte[] encodedKey = ((SecretJWK) jwk).toSecretKey().getEncoded();
                return new SecretKeySpec(encodedKey, alg);
            }
        } catch (JOSEException e) {
            return null;
        }
        return null;
    }

    public static Key toPublic(final JWK jwk) {
        try {
            if (jwk instanceof RSAKey) {
                KeyPair keyPair = ((RSAKey)jwk).toKeyPair();
                if (keyPair.getPublic() != null) {
                    return keyPair.getPublic();
                }
            } else {
                throw new BaseException("Cannot extract public key from non AssymetricJWK");
            }
        } catch (JOSEException e) {
            return null;
        }
        return null;
    }
}
