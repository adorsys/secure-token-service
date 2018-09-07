package de.adorsys.sts.token.tokenexchange.server;

import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.token.TokenCoreConfiguration;
import de.adorsys.sts.token.authentication.TokenAuthenticationConfiguration;
import de.adorsys.sts.token.tokenexchange.JwtTokenExchangeService;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClaimsService;
import de.adorsys.sts.token.tokenexchange.TokenExchangeService;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.token.tokenexchange.server"
})
@Import({TokenCoreConfiguration.class, TokenAuthenticationConfiguration.class})
public class TokenExchangeServerConfiguration {

    @Bean
    public TokenExchangeService tokenExchangeService(
            TokenExchangeClaimsService tokenExchangeClaimsService,
            KeyManagementService keyManagementService,
            BearerTokenValidator bearerTokenValidator
    ) {
        return new JwtTokenExchangeService(
                tokenExchangeClaimsService,
                keyManagementService,
                bearerTokenValidator
        );
    }
}
