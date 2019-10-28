package de.adorsys.sts.keymanagement;

import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.juggler.services.DaggerBCJuggler;
import de.adorsys.sts.keymanagement.persistence.CachedKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.security.Security;
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
        return new KeyConversionServiceImpl(keystore.getPassword());
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
            Juggler juggler,
            Clock clock,
            @Qualifier("enc") KeyPairGenerator encKeyPairGenerator,
            @Qualifier("sign") KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyStoreGeneratorImpl(
                juggler,
                clock,
                encKeyPairGenerator,
                signKeyPairGenerator,
                secretKeyGenerator,
                keyManagementProperties
        );
    }

    @Bean(name = "enc")
    KeyPairGenerator encKeyPairGenerator(
            Juggler juggler,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyPairGeneratorImpl(juggler, keyManagementProperties.getKeystore().getKeys().getEncKeyPairs());
    }

    @Bean(name = "sign")
    KeyPairGenerator signKeyPairGenerator(
            Juggler juggler,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyPairGeneratorImpl(juggler, keyManagementProperties.getKeystore().getKeys().getSignKeyPairs());
    }

    @Bean
    SecretKeyGenerator secretKeyGenerator(
            Juggler juggler,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new SecretKeyGeneratorImpl(
                juggler,
                keyManagementProperties.getKeystore().getKeys().getSecretKeys()
        );
    }

    @Bean
    KeyStoreInitializer keyStoreInitializer(
            @Qualifier("cached") KeyStoreRepository keyStoreRepository,
            KeyStoreGenerator keyStoreGenerator
    ) {
        return new KeyStoreInitializerImpl(keyStoreRepository, keyStoreGenerator);
    }

    @Bean
    Juggler juggler() {
        Security.addProvider(new BouncyCastleProvider());
        return DaggerBCJuggler.builder()
                .build();
    }
}
