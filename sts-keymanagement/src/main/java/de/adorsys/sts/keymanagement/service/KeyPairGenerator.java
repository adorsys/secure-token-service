package de.adorsys.sts.keymanagement.service;

import de.adorsys.sts.cryptoutils.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.KeyUsage;

import javax.security.auth.callback.CallbackHandler;
import java.security.KeyPair;

public class KeyPairGenerator {

    private final static int[] keyUsageSignature = {KeyUsage.nonRepudiation};
    private final static int[] keyUsageEncryption = {KeyUsage.keyEncipherment, KeyUsage.dataEncipherment, KeyUsage.keyAgreement};

    private final String keyAlgo;
    private final Integer keySize;
    private final String serverSigAlgo;
    private final String serverKeyPairName;

    public KeyPairGenerator(KeyManagementProperties.KeyStoreProperties.KeysProperties.KeyPairProperties keyProperties) {
        this.keyAlgo = keyProperties.getAlgo();
        this.keySize = keyProperties.getSize();
        this.serverSigAlgo = keyProperties.getSigAlgo();
        this.serverKeyPairName = keyProperties.getName();
    }

    public KeyPairEntry generateSignatureKey(String alias, CallbackHandler keyPassHandler) {
        return generate(keyUsageSignature, alias, keyPassHandler);
    }

    public KeyPairEntry generateEncryptionKey(String alias, CallbackHandler keyPassHandler) {
        return generate(keyUsageEncryption, alias, keyPassHandler);
    }

    private KeyPairEntry generate(int[] keyUsages, String alias, CallbackHandler keyPassHandler) {
        KeyPair keyPair = new KeyPairBuilder().withKeyAlg(keyAlgo).withKeyLength(keySize).build();
        X500Name dn = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.CN, serverKeyPairName).build();

        SelfSignedKeyPairData keyPairData = new SingleKeyUsageSelfSignedCertBuilder()
                .withSubjectDN(dn)
                .withSignatureAlgo(serverSigAlgo)
                .withNotAfterInDays(900)
                .withCa(false)
                .withKeyUsages(keyUsages)
                .build(keyPair);

        return KeyPairData.builder()
                .keyPair(keyPairData)
                .alias(alias)
                .passwordSource(keyPassHandler)
                .build();
    }
}
