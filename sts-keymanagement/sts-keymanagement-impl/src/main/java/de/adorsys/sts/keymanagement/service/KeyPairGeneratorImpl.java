package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.keymanagement.model.KeyPairEntry;

import javax.security.auth.callback.CallbackHandler;
import java.time.Clock;

public class KeyPairGeneratorImpl implements KeyPairGenerator {

    private final Clock clock;
    private final String keyAlgo;
    private final Integer keySize;
    private final String serverSigAlgo;
    private final String serverKeyPairName;

    public KeyPairGeneratorImpl(Clock clock,
                                KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties keyProperties) {
        this.clock = clock;
        this.keyAlgo = keyProperties.getAlgo();
        this.keySize = keyProperties.getSize();
        this.serverSigAlgo = keyProperties.getSigAlgo();
        this.serverKeyPairName = keyProperties.getName();
    }

    @Override
    public KeyPairEntry generateSignatureKey(String alias, CallbackHandler keyPassHandler) {
        return generate(new int[]{}, alias, keyPassHandler);
    }

    @Override
    public KeyPairEntry generateEncryptionKey(String alias, CallbackHandler keyPassHandler) {
        return generate(new int[]{}, alias, keyPassHandler);
    }

    private KeyPairEntry generate(int[] keyUsages, String alias, CallbackHandler keyPassHandler) {
        // FIXME-cleanup
        /*KeyPair keyPair = new KeyPairBuilder().withKeyAlg(keyAlgo).withKeyLength(keySize).build();
        X500Name dn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, serverKeyPairName).build();

        SelfSignedKeyPairData keyPairData = new SingleKeyUsageSelfSignedCertBuilder()
                .withSubjectDN(dn)
                .withSignatureAlgo(serverSigAlgo)
                .withNotAfterInDays(900)
                .withCa(false)
                .withKeyUsages(keyUsages)
                .withCreationDate(new Date(clock.instant().toEpochMilli()))
                .build(keyPair);

        return KeyPairData.builder()
                .keyPair(keyPairData)
                .alias(alias)
                .passwordSource(keyPassHandler)
                .build();*/
        return null;
    }
}
