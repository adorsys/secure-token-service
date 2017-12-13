package de.adorsys.sts.servicecomponentexample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceComponentResourceController {

    private final SecretClaimDecryptionService secretClaimDecryptionService;

    @Autowired
    public ServiceComponentResourceController(SecretClaimDecryptionService secretClaimDecryptionService) {
        this.secretClaimDecryptionService = secretClaimDecryptionService;
    }

    @GetMapping("/helloworld")
    public String helloWorld() {
        return secretClaimDecryptionService.decryptSecretClaim();
    }
}
