package de.adorsys.sts.secretserver.configuration;

import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.secretserver.EnableSecretServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableSecretServer
public class StsConfiguration {

    @Bean
    public ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }
}
