package de.adorsys.sts.persistence.jpa.config;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.bouncycastle.BouncyCastleProviderConfiguration;
import de.adorsys.sts.lock.ExecutionLockConfiguration;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import static de.adorsys.sts.lock.ExecutionLockConfiguration.DEFAULT_TABLE_KEY;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.persistence.jpa"
})
@Import({KeyManagementConfiguration.class, ExecutionLockConfiguration.class, BouncyCastleProviderConfiguration.class})
@EnableTransactionManagement
@EnableJpaRepositories("de.adorsys.sts.persistence.jpa.repository")
@EntityScan(
        basePackages = "de.adorsys.sts.persistence.jpa.entity",
        basePackageClasses = {Jsr310JpaConverters.class}
)
public class JpaConfiguration {

    @Bean
    LockProvider lockProvider(JdbcTemplate template, @Value(DEFAULT_TABLE_KEY) String lockTable) {
        return new JdbcTemplateLockProvider(template, lockTable);
    }
}
