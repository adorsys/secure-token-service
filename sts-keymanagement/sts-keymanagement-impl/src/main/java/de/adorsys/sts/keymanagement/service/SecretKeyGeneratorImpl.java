package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.template.generated.Secret;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKey;

import java.util.function.Supplier;

public class SecretKeyGeneratorImpl implements SecretKeyGenerator {

    private final Juggler juggler;
    private final String secretKeyAlgo;
    private final Integer keySize;

    public SecretKeyGeneratorImpl(Juggler juggler, String secretKeyAlgo, Integer keySize) {
        this.juggler = juggler;
        this.secretKeyAlgo = secretKeyAlgo;
        this.keySize = keySize;
    }

    public SecretKeyGeneratorImpl(Juggler juggler,
                                  KeyManagementProperties.KeyStoreProperties.KeysProperties.SecretKeyProperties secretKeyProperties) {
        this.juggler = juggler;
        this.secretKeyAlgo = secretKeyProperties.getAlgo();
        this.keySize = secretKeyProperties.getSize();
    }

    @Override
    public ProvidedKey generate(String alias, Supplier<char[]> keyPassword) {
        return juggler.generateKeys().secret(
                Secret.with()
                        .alias(alias)
                        .algo(secretKeyAlgo)
                        .keySize(keySize)
                        .password(keyPassword)
                        .build()
        );
    }
}
