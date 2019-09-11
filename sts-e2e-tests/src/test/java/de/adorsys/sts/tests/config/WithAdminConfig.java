package de.adorsys.sts.tests.config;

import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.resourceserver.initializer.EnableResourceServerInitialization;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnableAdmin
@EnableResourceServerInitialization
public class WithAdminConfig {

    @Primary
    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

}
