package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.persistence.CachedKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.time.Clock;

@Configuration
@ComponentScan(
        basePackages = {"de.adorsys.sts.keymanagement"},
        excludeFilters = @ComponentScan.Filter(
                pattern = "de.adorsys.sts.keymanagement.bouncycastle.*",
                type = FilterType.REGEX
        )
)
public class KeyManagementConfiguration {

    @Bean
    KeyConversionService keyConversionService(
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        KeyManagementProperties.KeyStoreProperties keystore = keyManagementProperties.getKeystore();

        if(keystore == null) {
            throw new RuntimeException("SHIT");
        }

        return new KeyConversionService(keystore.getPassword());
    }

    @Bean(name = "cached")
    KeyStoreRepository cachedKeyStoreRepository(KeyStoreRepository keyStoreRepository) {
        return new CachedKeyStoreRepository(keyStoreRepository);
    }

    @Bean
    KeyManagementService keyManagerService(
            @Qualifier("cached") KeyStoreRepository keyStoreRepository,
            KeyConversionService keyConversionService
    ) {
        return new KeyManagementService(
                keyStoreRepository,
                keyConversionService
        );
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    KeyStoreGenerator keyStoreGenerator(
            Clock clock,
            @Qualifier("enc") KeyPairGenerator encKeyPairGenerator,
            @Qualifier("sign") KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyStoreGenerator(
                clock,
                encKeyPairGenerator,
                signKeyPairGenerator,
                secretKeyGenerator,
                keyManagementProperties
        );
    }

    @Bean(name = "enc")
    KeyPairGenerator encKeyPairGenerator(
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyPairGenerator(keyManagementProperties.getKeystore().getKeys().getEncKeyPairs());
    }

    @Bean(name = "sign")
    KeyPairGenerator signKeyPairGenerator(
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyPairGenerator(keyManagementProperties.getKeystore().getKeys().getSignKeyPairs());
    }

    @Bean
    SecretKeyGenerator secretKeyGenerator(
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new SecretKeyGenerator(
                keyManagementProperties.getKeystore().getKeys().getSecretKeys()
        );
    }

    @Bean
    KeyStoreInitializer keyStoreInitializer(
            @Qualifier("cached") KeyStoreRepository keyStoreRepository,
            KeyStoreGenerator keyStoreGenerator
    ) {
        return new KeyStoreInitializer(keyStoreRepository, keyStoreGenerator);
    }
}
