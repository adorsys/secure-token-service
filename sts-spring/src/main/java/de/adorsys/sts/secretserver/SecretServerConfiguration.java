package de.adorsys.sts.secretserver;

import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import de.adorsys.sts.encryption.EncryptionConfiguration;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.objectmapper.JacksonConfiguration;
import de.adorsys.sts.pop.PopConfiguration;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.secret.SecretRepository;
import de.adorsys.sts.secretserver.encryption.EncryptedSecretRepository;
import de.adorsys.sts.simpleencryption.StaticKeyEncryptionFactory;
import de.adorsys.sts.token.authentication.TokenAuthenticationConfiguration;
import de.adorsys.sts.token.tokenexchange.*;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        TokenAuthenticationConfiguration.class,
        EncryptionConfiguration.class,
        PopConfiguration.class,
        JacksonConfiguration.class
})
public class SecretServerConfiguration {
    @Bean
    public TokenExchangeService tokenExchangeService(
            TokenExchangeClaimsService tokenExchangeClaimsService,
            KeyManagementService keyManagementService,
            BearerTokenValidator bearerTokenValidator
    ) {
        TokenExchangeService tokenExchangeService = new JwtTokenExchangeService(
                tokenExchangeClaimsService,
                keyManagementService,
                bearerTokenValidator
        );

        return new LoggingTokenExchangeService(tokenExchangeService);
    }

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
}
