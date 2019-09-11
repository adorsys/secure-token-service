package de.adorsys.sts.tests.config;

import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.tests.e2e.UserDataRepositoryImpl;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnablePasswordGrant
public class WithPasswordGrantConfig {

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

    @Bean
    UserDataRepository userDataRepository() {
        return new UserDataRepositoryImpl();
    }

}
