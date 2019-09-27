package de.adorsys.sts.cryptoutils;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import javax.security.auth.callback.CallbackHandler;

@Getter
public class KeyPairData extends KeyEntryData implements KeyPairEntry {

    @NonNull
    private final SelfSignedKeyPairData keyPair;

    @NonNull
    private final CertificationResult certification;

    @Builder
    private KeyPairData(CallbackHandler passwordSource, String alias, SelfSignedKeyPairData keyPair,
                        CertificationResult certification) {
        super(passwordSource, alias);
        this.keyPair = keyPair;
        this.certification = certification;
    }
}
