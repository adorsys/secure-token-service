package de.adorsys.sts.servicecomponentexample;

import de.adorsys.sts.keymanagement.service.SecretDecryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ServiceComponentResourceController {

    private final SecretDecryptionService secretDecryptionService;

    @Autowired
    public ServiceComponentResourceController(SecretDecryptionService secretDecryptionService) {
        this.secretDecryptionService = secretDecryptionService;
    }

    @GetMapping("/helloworld")
    public String helloWorld() {
        return secretDecryptionService.decryptSecretClaim();
    }
}
