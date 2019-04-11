package de.adorsys.sts.secretserver.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.NoopLockClient;
import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.keymanagement.persistence.InMemoryKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.secret.SecretRepository;
import de.adorsys.sts.secretserver.EnableSecretServer;
import de.adorsys.sts.secretserver.encryption.InMemorySecretRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan(basePackages = {"de.adorsys.sts.secretserver.helper"})
@Import(ITSecurityConfiguration.class)
@EnableSecretServer
@EnableDecryption
public class TestConfiguration {

    @Bean
    public ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

    @Bean
    public KeyStoreRepository keyStoreRepository() {
        return new InMemoryKeyStoreRepository();
    }

    @Bean
    public SecretRepository secretRepository() {
        return new InMemorySecretRepository();
    }

    @Bean
    public LockClient lockClient() {
        return new NoopLockClient();
    }

    @Bean
    @Primary
    @Qualifier("json")
    public ObjectMapper createJsonObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
