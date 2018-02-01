package de.adorsys.sts.jpa.example.config;

import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.SimpleLockClient;
import de.adorsys.lockpersistence.service.LockService;
import de.adorsys.sts.encryption.EnableEncryption;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.resourceserver.initializer.EnableResourceServerInitialization;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

@Configuration
@EnableResourceServerInitialization
@EnableEncryption
@EnablePOP
@EnableKeyRotation
@EnableJpaPersistence
public class ExampleConfiguration {

    @Bean
    public TaskScheduler taskExecutor () {
        return new ConcurrentTaskScheduler();
    }

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }
}
