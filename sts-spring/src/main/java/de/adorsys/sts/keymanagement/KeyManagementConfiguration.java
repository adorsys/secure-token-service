package de.adorsys.sts.keymanagement;

import com.google.gson.*;
import de.adorsys.keymanagement.api.Juggler;
import de.adorsys.keymanagement.api.config.keystore.KeyStoreConfig;
import de.adorsys.keymanagement.core.metadata.MetadataPersistenceConfig;
import de.adorsys.keymanagement.core.metadata.WithPersister;
import de.adorsys.keymanagement.juggler.services.DaggerBCJuggler;
import de.adorsys.sts.keymanagement.model.StsKeyEntryImpl;
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
import java.time.ZonedDateTime;

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
    Juggler juggler(KeyManagementProperties properties) {
        Security.addProvider(new BouncyCastleProvider());
        String keyStoreType = properties.getKeystore().getType();
        return DaggerBCJuggler.builder()
                .keyStoreConfig(KeyStoreConfig.builder()
                        .type(null == keyStoreType ? "UBER" : keyStoreType)
                        .build()
                )
                .metadataConfig(
                        MetadataPersistenceConfig.builder()
                                .metadataClass(StsKeyEntryImpl.class)
                                .gson(getGson())
                                .build()
                )
                .metadataPersister(new WithPersister())
                .build();
    }

    private Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        ZonedDateTime.class,
                        getZonedDateTimeJsonDeserializer()
                )
                .registerTypeAdapter(
                        ZonedDateTime.class,
                        getZonedDateTimeJsonSerializer()
                )
                .create();
    }

    private JsonDeserializer<ZonedDateTime> getZonedDateTimeJsonDeserializer() {
        return (json, type, jsonDeserializationContext) -> ZonedDateTime.parse(json.getAsJsonPrimitive().getAsString());
    }

    private JsonSerializer<ZonedDateTime> getZonedDateTimeJsonSerializer() {
        return (time, type, jsonDeserializationContext) -> new JsonPrimitive(time.toString());
    }
}
