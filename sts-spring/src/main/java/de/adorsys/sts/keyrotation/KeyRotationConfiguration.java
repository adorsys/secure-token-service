package de.adorsys.sts.keyrotation;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import de.adorsys.sts.keymanagement.service.KeyStoreFilter;
import de.adorsys.sts.keymanagement.service.KeyStoreGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ComponentScan("de.adorsys.sts.keyrotation")
@Import(KeyManagementConfiguration.class)
public class KeyRotationConfiguration {

    @Bean
    KeyRotationService keyRotationService(
            KeyStoreFilter keyStoreFilter,
            KeyStoreGenerator keyStoreGenerator,
            KeyRotationManagementConfigurationProperties keyManagementRotationProperties
    ) {
        return new KeyRotationService(
                keyStoreFilter,
                keyStoreGenerator,
                keyManagementRotationProperties
        );
    }
}
