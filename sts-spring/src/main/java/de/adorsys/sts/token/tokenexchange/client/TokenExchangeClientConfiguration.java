package de.adorsys.sts.token.tokenexchange.client;

import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TokenExchangeClientConfiguration {

    @Bean
    public TokenExchangeClient tokenExchangeClient(
            RestTemplate restTemplate
    ) {
        TokenExchangeClient tokenExchangeClient = new RestTokenExchangeClient(restTemplate);
        return new LoggingTokenExchangeClient(tokenExchangeClient);
    }

}
