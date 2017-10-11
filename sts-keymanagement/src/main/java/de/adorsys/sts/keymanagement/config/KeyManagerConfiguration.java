package de.adorsys.sts.keymanagement.config;

import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.keymanagement.service.KeyPairGenerator;
import de.adorsys.sts.keymanagement.service.KeyStoreGenerator;
import de.adorsys.sts.keymanagement.service.SecretKeyGenerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("de.adorsys.sts.keymanagement")
public class KeyManagerConfiguration {

    @Bean
    KeyManagementService keyManagerService(
            KeyStoreRepository keyStoreRepository,
            KeyStoreGenerator keyStoreGenerator,
            KeyManagementProperties keyManagementProperties
    ) {
        return new KeyManagementService(
                keyStoreRepository,
                keyStoreGenerator,
                keyManagementProperties.getKeystore().getPassword()
        );
    }

    @Bean
    KeyStoreGenerator keyStoreGenerator(
            @Qualifier("enc") KeyPairGenerator encKeyPairGenerator,
            @Qualifier("sign") KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementProperties keyManagementProperties
    ) {
        return new KeyStoreGenerator(
                encKeyPairGenerator,
                signKeyPairGenerator,
                secretKeyGenerator,
                keyManagementProperties
        );
    }

    @Bean(name = "enc")
    KeyPairGenerator encKeyPairGenerator(
            KeyManagementProperties keyManagementProperties
    ) {
        return new KeyPairGenerator(keyManagementProperties.getKeystore().getKeys().getEncKeyPairs());
    }

    @Bean(name = "sign")
    KeyPairGenerator signKeyPairGenerator(
            KeyManagementProperties keyManagementProperties
    ) {
        return new KeyPairGenerator(keyManagementProperties.getKeystore().getKeys().getSignKeyPairs());
    }

    @Bean
    SecretKeyGenerator secretKeyGenerator(
            KeyManagementProperties keyManagementProperties
    ) {
        return new SecretKeyGenerator(
                keyManagementProperties.getKeystore().getKeys().getSecretKeys()
        );
    }
}
