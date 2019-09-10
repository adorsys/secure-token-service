package de.adorsys.sts.persistence.mongo.config;

import com.mongodb.MongoClient;
import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.bouncycastle.BouncyCastleProviderConfiguration;
import de.adorsys.sts.lock.ExecutionLockConfiguration;
import de.adorsys.sts.persistence.mongo.repository.MongoKeyStoreRepository;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import static de.adorsys.sts.lock.ExecutionLockConfiguration.DEFAULT_TABLE_KEY;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.mongo"
})
@Import({KeyManagementConfiguration.class, ExecutionLockConfiguration.class, BouncyCastleProviderConfiguration.class})
@EnableMongoRepositories(
        basePackageClasses = MongoKeyStoreRepository.class
)
public class MongoConfiguration {

    @Bean
    LockProvider lockProvider(MongoClient client, @Value(DEFAULT_TABLE_KEY) String lockTable) {
        return new MongoLockProvider(client, lockTable);
    }
}
