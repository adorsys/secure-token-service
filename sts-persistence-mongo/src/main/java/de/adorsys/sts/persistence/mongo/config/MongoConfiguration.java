package de.adorsys.sts.persistence.mongo.config;

import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.SimpleLockClient;
import de.adorsys.lockpersistence.service.LockService;
import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.bouncycastle.BouncyCastleProviderConfiguration;
import de.adorsys.sts.persistence.mongo.repository.MongoKeyStoreRepository;
import de.adorysys.lockpersistence.mongo.config.EnableMongoLockPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.mongo"
})
@Import({KeyManagementConfiguration.class, BouncyCastleProviderConfiguration.class})
@EnableMongoRepositories(
        basePackageClasses = MongoKeyStoreRepository.class
)
@EnableMongoLockPersistence
public class MongoConfiguration {

    @Bean
    LockClient lockClient(LockService lockService) {
        return new SimpleLockClient("sts.lock", lockService);
    }
}
