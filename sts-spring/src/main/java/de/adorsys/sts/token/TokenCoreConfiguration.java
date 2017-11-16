package de.adorsys.sts.token;

import de.adorsys.sts.keymanagement.KeyManagerConfiguration;
import de.adorsys.sts.resourceserver.ResourceServerManagementConfiguration;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessor;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ResourceServerManagementConfiguration.class, KeyManagerConfiguration.class})
public class TokenCoreConfiguration {

    @Bean
    public ResourceServerProcessor resourceServerProcessor(
            ResourceServerService resourceServerService,
            EncryptionService encryptionService
    ) {
        return new ResourceServerProcessor(resourceServerService, encryptionService);
    }
}
