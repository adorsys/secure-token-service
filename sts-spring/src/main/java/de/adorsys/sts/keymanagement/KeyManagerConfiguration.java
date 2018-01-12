package de.adorsys.sts.keymanagement;

import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.util.Map;

@Configuration
@EnableScheduling
@ComponentScan("de.adorsys.sts.keymanagement")
public class KeyManagerConfiguration implements ImportAware {

    @Autowired
    private KeyRotationSchedule keyRotationSchedule;

    @Bean
    KeyConversionService keyConversionService(
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new KeyConversionService(keyManagementProperties.getKeystore().getPassword());
    }

    @Bean
    KeyRotationService keyRotationService(
            KeyStoreFilter keyStoreFilter,
            KeyStoreGenerator keyStoreGenerator,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        KeyManagementProperties.KeyStoreProperties.KeysProperties keysProperties = keyManagementProperties.getKeystore().getKeys();

        return new KeyRotationService(
                keyStoreFilter,
                keyStoreGenerator,
                keysProperties.getEncKeyPairs().getRotation(),
                keysProperties.getSignKeyPairs().getRotation(),
                keysProperties.getSecretKeys().getRotation()
        );
    }

    @Bean
    KeyStoreFilter keyStoreFilter() {
        return new KeyStoreFilter(Clock.systemUTC());
    }

    @Bean
    KeyManagementService keyManagerService(
            KeyStoreRepository keyStoreRepository,
            KeyStoreGenerator keyStoreGenerator,
            KeyConversionService keyConversionService,
            KeyStoreFilter keyStoreFilter
    ) {
        return new KeyManagementService(
                keyStoreRepository,
                keyStoreGenerator,
                keyConversionService,
                keyStoreFilter
        );
    }

    @Bean
    KeyStoreGenerator keyStoreGenerator(
            @Qualifier("enc") KeyPairGenerator encKeyPairGenerator,
            @Qualifier("sign") KeyPairGenerator signKeyPairGenerator,
            SecretKeyGenerator secretKeyGenerator,
            KeyManagementConfigurationProperties keyManagementProperties
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

//    @Bean
//    KeyRotationSchedule keyRotationSchedule(
//            KeyRotationService keyRotationService,
//            KeyStoreRepository keyStoreRepository
//    ) {
//        return new KeyRotationSchedule(keyRotationService, keyStoreRepository);
//    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        configureKeyManagementByAnnotation(importMetadata);
    }

    private void configureKeyManagementByAnnotation(AnnotationMetadata importMetadata) {
        Map<String, Object> annotationAttributesMap = importMetadata
                .getAnnotationAttributes(EnableKeyManagement.class.getName());
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(annotationAttributesMap);

        boolean isKeyRotationEnabled = false;
        if(annotationAttributes != null) {
            isKeyRotationEnabled = annotationAttributes.getBoolean("keyRotationEnabled");
        }

        if (keyRotationSchedule != null) {
            keyRotationSchedule.setEnabled(isKeyRotationEnabled);
        }
    }
}
