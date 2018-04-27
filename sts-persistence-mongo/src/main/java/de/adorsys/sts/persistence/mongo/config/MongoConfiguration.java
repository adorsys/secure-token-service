package de.adorsys.sts.persistence.mongo.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.SimpleLockClient;
import de.adorsys.lockpersistence.service.LockService;
import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.bouncycastle.BouncyCastleProviderConfiguration;
import de.adorysys.lockpersistence.mongo.config.EnableMongoLockPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.mongo",
})
@Import({KeyManagementConfiguration.class, BouncyCastleProviderConfiguration.class})
@EnableTransactionManagement
@EnableMongoRepositories(basePackages = "de.adorsys.sts.persistence.mongo.repository")
@EnableMongoLockPersistence
@Profile({"mongo"})
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.database:sts}")
    private String databaseName;

    @Autowired(required = false)
    private MongoClient mongoClient;

    @Bean
    LockClient lockClient(LockService lockService) {
        return new SimpleLockClient("sts.lock", lockService);
    }

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Override
    public MongoClient mongoClient() {
        if(mongoClient == null) {
            return new MongoClient();
        }

        return mongoClient;
    }
}
