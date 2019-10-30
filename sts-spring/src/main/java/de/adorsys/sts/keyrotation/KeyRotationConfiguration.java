package de.adorsys.sts.keyrotation;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keymanagement.service.KeyRotationService;
import de.adorsys.sts.keymanagement.service.KeyRotationServiceImpl;
import de.adorsys.sts.keymanagement.service.KeyStoreGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;

@Configuration
@EnableScheduling
@ComponentScan("de.adorsys.sts.keyrotation")
@Import(KeyManagementConfiguration.class)
public class KeyRotationConfiguration {

    @Bean
    KeyRotationService keyRotationService(
            Clock clock,
            KeyStoreGenerator keyStoreGenerator,
            KeyManagementProperties properties,
            KeyRotationManagementConfigurationProperties keyManagementRotationProperties
    ) {
        return new KeyRotationServiceImpl(
                keyStoreGenerator,
                clock,
                properties.getKeystore(),
                keyManagementRotationProperties
        );
    }
}
