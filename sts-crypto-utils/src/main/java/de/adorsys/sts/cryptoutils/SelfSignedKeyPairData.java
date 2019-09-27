package de.adorsys.sts.cryptoutils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.X509CertificateHolder;

import java.security.KeyPair;

@Getter
@RequiredArgsConstructor
public class SelfSignedKeyPairData {

	@NonNull
    private final KeyPair keyPair;

	@NonNull
	private final X509CertificateHolder subjectCert;
}
