package de.adorsys.sts.keymanagement.service;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.types.template.generated.Encrypting;
import de.adorsys.keymanagement.api.types.template.generated.Signing;
import de.adorsys.keymanagement.api.types.template.provided.ProvidedKeyPair;

import java.util.function.Supplier;

public class KeyPairGeneratorImpl implements KeyPairGenerator {

    private final Juggler juggler;
    private final String keyAlgo;
    private final Integer keySize;
    private final String serverSigAlgo;
    private final String serverKeyPairName;

    public KeyPairGeneratorImpl(Juggler juggler,
                                KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties keyProperties) {
        this.juggler = juggler;
        this.keyAlgo = keyProperties.getAlgo();
        this.keySize = keyProperties.getSize();
        this.serverSigAlgo = keyProperties.getSigAlgo();
        this.serverKeyPairName = keyProperties.getName();
    }

    @Override
    public ProvidedKeyPair generateSignatureKey(String alias, Supplier<char[]> keyPassword) {
        return juggler.generateKeys()
                .signing(
                        Signing.with()
                                .alias(alias)
                                .algo(keyAlgo)
                                .keySize(keySize)
                                .sigAlgo(serverSigAlgo)
                                .commonName(serverKeyPairName)
                                .password(keyPassword)
                                .build()
                );
    }

    @Override
    public ProvidedKeyPair generateEncryptionKey(String alias, Supplier<char[]> keyPassword) {
        return juggler.generateKeys()
                .encrypting(
                        Encrypting.with()
                                .alias(alias)
                                .algo(keyAlgo)
                                .keySize(keySize)
                                .sigAlgo(serverSigAlgo)
                                .commonName(serverKeyPairName)
                                .password(keyPassword)
                                .build()
                );
    }
}
