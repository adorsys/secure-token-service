package de.adorsys.sts.decryption;

import de.adorsys.sts.keymanagement.KeyManagementConfiguration;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.keymanagement.service.DecryptionServiceImpl;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan(
        basePackages = {"de.adorsys.sts.decryption"},
        excludeFilters = @ComponentScan.Filter(
                pattern = "de.adorsys.sts.decryption.secret.*",
                type = FilterType.REGEX
        )
)
@Import(KeyManagementConfiguration.class)
public class DecryptionConfiguration {

    @Bean
    public DecryptionService decryptionService(
            KeyManagementService keyManagementService
    ) {
        return new DecryptionServiceImpl(keyManagementService);
    }
}
