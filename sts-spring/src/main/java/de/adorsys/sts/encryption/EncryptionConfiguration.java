package de.adorsys.sts.encryption;

import de.adorsys.sts.resourceserver.ResourceServerManagementConfiguration;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ResourceServerManagementConfiguration.class)
public class EncryptionConfiguration {

    @Bean
    public KeyRetrieverService keyRetrieverService(
            ResourceServerService resourceServerService
    ) {
        return new KeyRetrieverService(resourceServerService);
    }

    @Bean
    public EncryptionService encryptionService(
            KeyRetrieverService keyRetrieverService
    ) {
        return new EncryptionService(keyRetrieverService);
    }
}
