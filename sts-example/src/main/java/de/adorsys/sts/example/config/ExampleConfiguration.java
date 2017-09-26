package de.adorsys.sts.example.config;

import de.adorsys.sts.example.service.ResourceServerPropertiesProvider;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleConfiguration {

    @Bean
    ResourceServerService resourceServerService(
            ResourceServerPropertiesProvider configuration
    ) {
        return new ResourceServerService(configuration);
    }

    @Bean
    KeyRetrieverService keyRetrieverService(
            ResourceServerService resourceServerService
    ) {
        return new KeyRetrieverService(resourceServerService);
    }

    @Bean
    EncryptionService encryptionService(
            KeyRetrieverService keyRetrieverService
    ) {
        return new EncryptionService(keyRetrieverService);
    }
}
