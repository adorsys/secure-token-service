package de.adorsys.sts.token.passwordgrant;

import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.objectmapper.JacksonObjectMapper;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessor;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessorService;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.token.TokenCoreConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.token.passwordgrant",
        "de.adorsys.sts.resourceserver.processing"
})
@Import(TokenCoreConfiguration.class)
public class PasswordGrantConfiguration {

    @Bean
    public PasswordGrantService passwordGrantService(
            KeyManagementService keyManagementService,
            ResourceServerProcessorService resourceServerProcessorService
    ) {
        return new PasswordGrantService(keyManagementService, resourceServerProcessorService, new JacksonObjectMapper());
    }

    @Bean
    public ResourceServerProcessorService resourceServerProcessorService(
            ResourceServerProcessor resourceServerProcessor,
            UserDataRepository userDataRepository
    ) {
    	return new ResourceServerProcessorService(resourceServerProcessor, userDataRepository);
    }
}
