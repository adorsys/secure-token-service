package de.adorsys.sts.secretserverclient;

import de.adorsys.sts.decryption.DecryptionConfiguration;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.secret.CachingSecretServerClient;
import de.adorsys.sts.secret.LoggingSecretServerClient;
import de.adorsys.sts.secret.SecretServerClient;
import de.adorsys.sts.token.secretserver.TokenExchangeSecretServerClient;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import de.adorsys.sts.token.tokenexchange.client.TokenExchangeClientConfiguration;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.secretserverclient"
})
@Import({
        TokenExchangeClientConfiguration.class,
        DecryptionConfiguration.class
})
public class SecretServerClientConfiguration {

    @Bean
    public SecretServerClient secretServerClient(
            @Value("${sts.secret-server-client.audience}") String audience,
            @Value("${sts.secret-server-client.secret-server-uri}") String secretServerUri,
            TokenExchangeClient tokenExchangeClient,
            BearerTokenValidator bearerTokenValidator,
            DecryptionService decryptionService,
            @Value("${sts.secret-server-client.cache.enabled:false}") Boolean isCacheEnabled,
            @Value("${sts.secret-server-client.cache.maximum-size:1000}") Integer maximumSize,
            @Value("${sts.secret-server-client.cache.expire-after-access:10}") Integer expireAfterAccessInMinutes
    ) {
        SecretServerClient secretServerClient = new TokenExchangeSecretServerClient(
                audience,
                secretServerUri,
                tokenExchangeClient,
                bearerTokenValidator,
                decryptionService
        );

        if(isCacheEnabled) {
            secretServerClient = new CachingSecretServerClient(
                    secretServerClient,
                    maximumSize,
                    expireAfterAccessInMinutes
            );
        }

        return new LoggingSecretServerClient(secretServerClient);
    }
}
