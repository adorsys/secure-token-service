package de.adorsys.sts.keystore;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.AssymetricJWK;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.PasswordLookup;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.SecretJWK;

import de.adorsys.sts.keystore.ServerKeyMap.KeyAndJwk;

public class KeyConverter {

    /**
     *
     * @param jwkList The JWK list. May be {@code null}.
     *
     * @return The converted keys, empty set if none or {@code null}.
     */

    /**
     * Converts the specified of JSON Web Keys (JWK) it's standard Java
     * class representation. Asymmetric {@link RSAKey RSA} and
     * {@link ECKey EC key} pairs are converted to
     * {@link java.security.PublicKey} and {@link java.security.PrivateKey}
     * (if specified) objects. {@link OctetSequenceKey secret JWKs} are
     * converted to {@link javax.crypto.SecretKey} objects.
     *
     * @param jwk
     * @return private key, secret key or nul;
     */
    public static Key toPrivateOrSecret(final JWK jwk) {
        try {
            if (jwk instanceof AssymetricJWK) {
                KeyPair keyPair = ((AssymetricJWK)jwk).toKeyPair();
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

    public static JWKSet exportPrivateKeys(KeyStore keyStore, char[] keypass) {
        PasswordLookup pwLookup = name -> keypass;
        try {
            return JWKSet.load(keyStore, pwLookup);
        } catch (KeyStoreException e) {
            throw new IllegalStateException(e);
        }
    }

	public static JWSAlgorithm getJWSAlgo(KeyAndJwk randomKey) {
		Algorithm algorithm = randomKey.jwk.getAlgorithm();
		if(algorithm!=null && (algorithm instanceof JWSAlgorithm)) return (JWSAlgorithm) algorithm;
		
		KeyType keyType = randomKey.jwk.getKeyType();
		if(keyType!=null){
			if(KeyType.RSA.equals(keyType)){
				return JWSAlgorithm.RS256;
			} else if(KeyType.EC.equals(keyType)){
				return JWSAlgorithm.ES256;
			} else if(KeyType.OCT.equals(keyType)){
				return JWSAlgorithm.HS256;
			} else {
				throw new IllegalStateException("Unknown key type: " + keyType);
			}
		} else {
			if(randomKey.jwk instanceof RSAKey){
				return JWSAlgorithm.RS256;
			} else if (randomKey.jwk instanceof ECKey){
				return JWSAlgorithm.ES256;
			} else if (randomKey.jwk instanceof OctetSequenceKey){
				return JWSAlgorithm.HS256;
			} else {
				throw new IllegalStateException("Unknown key type: " + randomKey.jwk.getClass().getName());				
			}
		}
	}
	
	public static JWSSigner findSigner(KeyAndJwk randomKey) throws JOSEException{
		if(randomKey.jwk instanceof RSAKey){
			return new RSASSASigner((RSAKey)randomKey.jwk);
		} else if (randomKey.jwk instanceof ECKey){
			return new ECDSASigner((ECKey)randomKey.jwk);
		} else if (randomKey.jwk instanceof OctetSequenceKey){
			return new MACSigner((OctetSequenceKey)randomKey.jwk);
		} else {
			throw new IllegalStateException("Unknown key type: " + randomKey.jwk.getClass().getName());				
		}
	}
}
