package de.adorsys.sts.secretserver;

import de.adorsys.sts.encryption.EncryptionConfiguration;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.pop.PopConfiguration;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.secret.SecretRepository;
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
        PopConfiguration.class
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
    public TokenExchangeClaimsService tokenExchangeSecretClaimsService(
            @Value("${sts.secret-server.secret-length:256}") Integer secretLengthInBits,
            SecretRepository secretRepository,
            EncryptionService encryptionService,
            ResourceServerService resourceServerService
    ) {
        TokenExchangeClaimsService tokenExchangeSecretClaimsService = new TokenExchangeSecretClaimsService(
                secretLengthInBits,
                secretRepository,
                encryptionService,
                resourceServerService
        );

        return new LoggingTokenExchangeClaimsService(tokenExchangeSecretClaimsService);
    }
}
