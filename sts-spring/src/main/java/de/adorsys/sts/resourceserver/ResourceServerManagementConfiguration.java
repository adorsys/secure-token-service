package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessor;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("de.adorsys.sts.resourceserver.service")
public class ResourceServerManagementConfiguration {

    @Bean
    public ResourceServerService resourceServerService(
            ResourceServerRepository repository
    ) {
        return new ResourceServerService(repository);
    }

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

    @Bean
    public ResourceServerProcessor resourceServerProcessor(
            ResourceServerService resourceServerService,
            EncryptionService encryptionService
    ) {
        return new ResourceServerProcessor(resourceServerService, encryptionService);
    }
}
