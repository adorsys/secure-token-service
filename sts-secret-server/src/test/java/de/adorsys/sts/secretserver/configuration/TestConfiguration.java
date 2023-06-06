package de.adorsys.sts.secretserver.configuration;

import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.secretserver.EnableSecretServer;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.adorsys.sts.secretserver.helper"})
@EnableSecretServer
@EnableDecryption
public class TestConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
