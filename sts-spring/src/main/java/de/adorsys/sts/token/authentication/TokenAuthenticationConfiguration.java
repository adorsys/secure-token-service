package de.adorsys.sts.token.authentication;

import de.adorsys.sts.tokenauth.AuthServersProvider;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import java.time.Clock;

@Configuration
@ComponentScan(
        basePackages = {"de.adorsys.sts.token.authentication"},
        excludeFilters = @ComponentScan.Filter(
                pattern = "de.adorsys.sts.token.authentication.securitycontext.*",
                type = FilterType.REGEX
        )
)
public class TokenAuthenticationConfiguration {

    @Bean
    BearerTokenValidator bearerTokenValidator(AuthServersProvider authServersProvider, Clock clock) {
        return new LoggingBearerTokenValidator(authServersProvider, clock);
    }
}
