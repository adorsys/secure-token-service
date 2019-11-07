package de.adorsys.sts.pop;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.keymanagement.service.ServerKeyMapProvider;

public class PopService {

    private final ServerKeyMapProvider keyManagementService;

    public PopService(ServerKeyMapProvider keyManagementService) {
        this.keyManagementService = keyManagementService;
    }

    public JWKSet getPublicKeys(){
        return keyManagementService.getPublicKeys();
    }
}
