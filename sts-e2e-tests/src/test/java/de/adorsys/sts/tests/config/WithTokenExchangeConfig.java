package de.adorsys.sts.tests.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import de.adorsys.sts.objectmapper.JacksonConfiguration;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.secret.SecretRepository;
import de.adorsys.sts.secretserver.encryption.EncryptedSecretRepository;
import de.adorsys.sts.simpleencryption.StaticKeyEncryptionFactory;
import de.adorsys.sts.tests.e2e.tokenexchange.AuthServersProviderTestable;
import de.adorsys.sts.token.authentication.AuthServerConfigurationProperties;
import de.adorsys.sts.token.tokenexchange.LoggingTokenExchangeClaimsService;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClaimsService;
import de.adorsys.sts.token.tokenexchange.TokenExchangeSecretClaimsService;
import de.adorsys.sts.token.tokenexchange.server.EnableTokenExchangeServer;
import de.adorsys.sts.tokenauth.AuthServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration(exclude = MongoAutoConfiguration.class)
@EnableTokenExchangeServer
@Import({
        JacksonConfiguration.class
})
public class WithTokenExchangeConfig {

    @Bean
    public StaticKeyEncryptionFactory StaticKeyEncryptionFactory(
            ObjectMapperSPI objectMapper
    ) {
        return new StaticKeyEncryptionFactory(objectMapper);
    }

    @Bean
    public TokenExchangeClaimsService tokenExchangeSecretClaimsService(
            @Value("${sts.secret-server.secret-length:256}") Integer secretLengthInBits,
            @Value("${sts.secret-server.encryption.enabled:false}") Boolean isEncryptionEnabled,
            @Value("${sts.secret-server.encryption.algorithm:A256GCMKW}") String encryptionAlgorithm,
            @Value("${sts.secret-server.encryption.encryption-method:A256GCM}") String encryptionMethod,
            @Value("${sts.secret-server.encryption.key:}") String encryptionKey,
            StaticKeyEncryptionFactory staticKeyEncryptionFactory,
            SecretRepository secretRepository,
            EncryptionService encryptionService,
            ResourceServerService resourceServerService
    ) {
        if(isEncryptionEnabled) {
            secretRepository = new EncryptedSecretRepository(
                    secretRepository,
                    staticKeyEncryptionFactory.create(encryptionAlgorithm, encryptionMethod, encryptionKey)
            );
        }

        TokenExchangeClaimsService tokenExchangeSecretClaimsService = new TokenExchangeSecretClaimsService(
                secretLengthInBits,
                secretRepository,
                encryptionService,
                resourceServerService
        );

        return new LoggingTokenExchangeClaimsService(tokenExchangeSecretClaimsService);
    }

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

    @Autowired
    AuthServerConfigurationProperties authServerConfigurationProperties;
    @Autowired
    ObjectMapper objectMapper;

    @Bean
    AuthServersProvider authServersProvider() {
        return new AuthServersProviderTestable(authServerConfigurationProperties, objectMapper);
    }
}
