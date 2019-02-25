package de.adorsys.sts.decryption.secret;

import de.adorsys.sts.decryption.DecryptionConfiguration;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.keymanagement.service.SecretDecryptionService;
import de.adorsys.sts.keymanagement.service.SecretProvider;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(
        basePackages = {"de.adorsys.sts.decryption.secret"},
        excludeFilters = @ComponentScan.Filter(
                pattern = "de.adorsys.sts.decryption.secret.secretclaim.*",
                type = FilterType.REGEX
        )
)
@Import(DecryptionConfiguration.class)
public class SecretDecryptionConfiguration {

    @Bean
    SecretDecryptionService secretDecryptionService(
            DecryptionService decryptionService,
            SecretProvider secretProvider
    ) {
        return new SecretDecryptionService(
                decryptionService,
                secretProvider
        );
    }
}
