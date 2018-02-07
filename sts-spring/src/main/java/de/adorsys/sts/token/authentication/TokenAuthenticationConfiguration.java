package de.adorsys.sts.token.authentication;

import de.adorsys.sts.tokenauth.AuthServersProvider;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.token.authentication"
})
public class TokenAuthenticationConfiguration {

    @Bean
    public BearerTokenValidator bearerTokenValidator(AuthServersProvider authServersProvider) {
        return new BearerTokenValidator(authServersProvider);
    }
}
