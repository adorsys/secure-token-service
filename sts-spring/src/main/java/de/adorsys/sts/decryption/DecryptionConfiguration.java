package de.adorsys.sts.decryption;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(KeyManagementConfiguration.class)
public class DecryptionConfiguration {

    @Bean
    public DecryptionService decryptionService(
            KeyManagementService keyManagementService
    ) {
        return new DecryptionService(keyManagementService);
    }
}
