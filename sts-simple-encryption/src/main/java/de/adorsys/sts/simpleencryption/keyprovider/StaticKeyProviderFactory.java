package de.adorsys.sts.simpleencryption.keyprovider;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import de.adorsys.sts.cryptoutils.JWEEncryptedSelector;
import de.adorsys.sts.simpleencryption.KeyProvider;
import org.apache.commons.lang3.StringUtils;

public class StaticKeyProviderFactory {

    private StaticKeyProviderFactory() {
        throw new UnsupportedOperationException();
    }

    public static KeyProvider createKeyProvider(String jweAlgorithm, String jweEncryptionMethod, String key) {
        KeyProvider keyProvider;

        if(StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Encryption key must not be null or empty");
        }

        JWEAlgorithm parsedAlgorithm = JWEAlgorithm.parse(jweAlgorithm);
        EncryptionMethod parsedEncryptionMethod = EncryptionMethod.parse(jweEncryptionMethod);

        if(JWEEncryptedSelector.isSupportedByAesCrypter(parsedAlgorithm, parsedEncryptionMethod)) {
            keyProvider = new StaticAesKeyProvider(key);
        } else if(JWEEncryptedSelector.isSupportedByRsaCrypter(parsedAlgorithm, parsedEncryptionMethod)) {
            keyProvider = new StaticRsaKeyProvider(key);
        } else {
            throw new IllegalArgumentException("JWEAlgorithm " +  jweAlgorithm + " or EncryptionMethod " + jweEncryptionMethod + " not supported");
        }

        return keyProvider;
    }
}
