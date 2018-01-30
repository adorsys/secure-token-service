package de.adorsys.sts.persistence.jpa.config;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.jpa",
})
@Import(KeyManagementConfiguration.class)
@EnableTransactionManagement
@EnableJpaRepositories("de.adorsys.sts.persistence.jpa.repository")
@EntityScan(
        basePackages = "de.adorsys.sts.persistence.jpa.entity",
        basePackageClasses = {Jsr310JpaConverters.class}
)
public class JpaConfiguration {
}
