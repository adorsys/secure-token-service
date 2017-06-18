package de.adorsys.sts.keystore;

import java.security.Key;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;

import com.nimbusds.jose.jwk.AssymetricJWK;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.SecretJWK;

public class ServerKeyMap {
    private Map<String, KeyAndJwk> keyMap = new HashMap<>();
    private List<KeyAndJwk> signKeyeyList = new ArrayList<>();
    private List<KeyAndJwk> encKeyeyList = new ArrayList<>();
    private List<KeyAndJwk> secretKeyeyList = new ArrayList<>();
    
    public ServerKeyMap(JWKSet jwkSet){
        List<JWK> keys = jwkSet.getKeys();
        for (JWK jwk : keys) {
            if (jwk instanceof AssymetricJWK) {
            	Key key = KeyConverter.toPrivateOrSecret(jwk);
            	if(key!=null && jwk.getKeyID()!=null){
            		KeyAndJwk keyAndJwk = new KeyAndJwk(key, jwk);
            		keyMap.put(jwk.getKeyID(), keyAndJwk);
            		if(KeyUse.SIGNATURE.equals(jwk.getKeyUse())){
            			signKeyeyList.add(keyAndJwk);
            		} else if (KeyUse.ENCRYPTION.equals(jwk.getKeyUse())){
            			encKeyeyList.add(keyAndJwk);
            		}
            	}
            } else if (jwk instanceof SecretJWK) {
            	Key key = KeyConverter.toPrivateOrSecret(jwk);
            	if(key!=null && jwk.getKeyID()!=null){
            		KeyAndJwk keyAndJwk = new KeyAndJwk(key, jwk);
            		keyMap.put(jwk.getKeyID(), keyAndJwk);
            		secretKeyeyList.add(keyAndJwk);
            	}
            }

        }
    }

    public static class KeyAndJwk {
        public final Key key;
        public final JWK jwk;
        public KeyAndJwk(Key key, JWK jwk) {
            this.key = key;
            this.jwk = jwk;
        }
    }

    private KeyAndJwk get(String keyID){
        if(keyID==null) return null;
        KeyAndJwk keyAndJwk = keyMap.get(keyID);
        if(keyAndJwk==null) return null;
        if(!keyID.equalsIgnoreCase(keyAndJwk.jwk.getKeyID()))return null;
        return keyAndJwk;
    }

    public Key getKey(String keyID){
        KeyAndJwk keyAndJwk = get(keyID);
        if(keyAndJwk==null) return null;
        return keyAndJwk.key;
    }

	
	/**
	 * Select a random key by random picking a number between 0 (inclusive) and size exclusive;
	 */
    public KeyAndJwk randomSignKey(){
    	int nextInt = RandomUtils.nextInt(0, signKeyeyList.size());
    	return signKeyeyList.get(nextInt);
    }

	public KeyAndJwk randomSecretKey() {
    	int nextInt = RandomUtils.nextInt(0, secretKeyeyList.size());
    	return secretKeyeyList.get(nextInt);
	}
    
}
