package de.adorsys.sts.encryption;

import de.adorsys.sts.resourceserver.ResourceServerManagementConfiguration;
import de.adorsys.sts.resourceserver.ResourceServerManagementConfigurationProperties;
import de.adorsys.sts.resourceserver.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ResourceServerManagementConfiguration.class)
public class EncryptionConfiguration {

    @Bean
    public KeyRetrieverService keyRetrieverService(
            ResourceServerService resourceServerService,
            ResourceServerManagementConfigurationProperties resourceServerManagementConfigurationProperties
    ) {
        return new KeyRetrieverService(resourceServerService, resourceServerManagementConfigurationProperties);
    }

    @Bean
    public EncryptionService encryptionService(
            KeyRetrieverService keyRetrieverService
    ) {
        EncryptionService encryptionService = new JweEncryptionService(keyRetrieverService);
        return new LoggingEncryptionService(encryptionService);
    }
}
