package de.adorsys.sts.simpleencryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import de.adorsys.sts.simpleencryption.decrypt.JweDecrypter;
import de.adorsys.sts.simpleencryption.encrypt.JweEncrypter;
import de.adorsys.sts.simpleencryption.keyprovider.StaticKeyProviderFactory;

public class StaticKeyEncryptionFactory {

    private final ObjectMapperSPI objectMapper;

    public StaticKeyEncryptionFactory(ObjectMapperSPI objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ObjectEncryption create(String alg, String enc, String key) {
        KeyProvider keyProvider = StaticKeyProviderFactory.createKeyProvider(alg, enc, key);

        JWEAlgorithm jweAlgorithm = JWEAlgorithm.parse(alg);
        EncryptionMethod jweEncryptionMethod = EncryptionMethod.parse(enc);

        return new JsonMappedObjectEncryption(
                objectMapper,
                new JweEncrypter(keyProvider, jweAlgorithm, jweEncryptionMethod),
                new JweDecrypter(keyProvider)
        );
    }
}
