package de.adorsys.sts.example.config;

import de.adorsys.sts.encryption.EnableEncryption;
import de.adorsys.sts.resourceserver.initializer.EnableResourceServerInitialization;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableResourceServerInitialization
@EnableEncryption
public class ExampleConfiguration {

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }
}
