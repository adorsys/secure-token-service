package de.adorsys.sts.cryptoutils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.cert.X509CertificateHolder;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CertificationResult {

	@NonNull
	private final X509CertificateHolder subjectCert;

	@NonNull
    private final List<X509CertificateHolder> issuerChain;
}
