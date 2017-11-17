package de.adorsys.sts.resourceserver;

import de.adorsys.sts.encryption.EncryptionConfiguration;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessor;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EncryptionConfiguration.class)
public class ResourceServerProcessingConfiguration {

    @Bean
    public ResourceServerProcessor resourceServerProcessor(
            ResourceServerService resourceServerService,
            EncryptionService encryptionService
    ) {
        return new ResourceServerProcessor(resourceServerService, encryptionService);
    }
}
