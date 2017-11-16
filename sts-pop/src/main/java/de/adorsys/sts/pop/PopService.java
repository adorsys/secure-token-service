package de.adorsys.sts.pop;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.keymanagement.service.KeyManagementService;

public class PopService {

    private final KeyManagementService keyManagementService;

    public PopService(KeyManagementService keyManagementService) {
        this.keyManagementService = keyManagementService;
    }

    public JWKSet getPublicKeys(){
        JWKSet publicKeySet = keyManagementService.getServerKeysHolder().getPublicKeySet();
        return publicKeySet.toPublicJWKSet();
    }
}
