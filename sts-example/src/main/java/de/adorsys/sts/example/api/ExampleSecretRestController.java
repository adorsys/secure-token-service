package de.adorsys.sts.example.api;

import de.adorsys.sts.keymanagement.service.SecretProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleSecretRestController {

    private static final Logger logger = LoggerFactory.getLogger(ExampleSecretRestController.class);

    @Autowired
    private SecretProvider secretProvider;

    @GetMapping("/secret")
    public String secret() {
        if(logger.isTraceEnabled()) logger.trace("GET /secret start...");

        String Secret = secretProvider.get();

        if(logger.isTraceEnabled()) logger.trace("GET /secret finish.");

        return Secret;
    }
}
