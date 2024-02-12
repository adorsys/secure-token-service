package de.adorsys.sts.example.config;

import de.adorsys.sts.encryption.EnableEncryption;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.resourceserver.initializer.EnableResourceServerInitialization;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.secretserverclient.EnableSecretServerClient;
import de.adorsys.sts.token.authentication.EnableTokenAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableResourceServerInitialization
@EnableEncryption
@EnablePOP
//@EnableKeyRotation
@EnableTokenAuthentication
@EnableSecretServerClient
public class StsConfiguration {

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }
}
