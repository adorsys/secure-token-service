package de.adorsys.sts.persistence.jpa.config;

import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.SimpleLockClient;
import de.adorsys.lockpersistence.jpa.config.EnableJpaLockPersistence;
import de.adorsys.lockpersistence.service.LockService;
import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.bouncycastle.BouncyCastleProviderConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.jpa",
})
@Import({KeyManagementConfiguration.class, BouncyCastleProviderConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories("de.adorsys.sts.persistence.jpa.repository")
@EntityScan(
        basePackages = "de.adorsys.sts.persistence.jpa.entity",
        basePackageClasses = {Jsr310JpaConverters.class}
)
@EnableJpaLockPersistence
@Profile({"!mongo"})
public class JpaConfiguration {

    @Bean
    LockClient lockClient(LockService lockService) {
        return new SimpleLockClient("sts.lock", lockService);
    }
}
