package de.adorsys.sts.pop;

import de.adorsys.sts.keymanagement.KeyManagerConfiguration;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.pop"
})
@Import(KeyManagerConfiguration.class)
public class PopConfiguration {

    @Bean
    public PopService popService(
            KeyManagementService keyManagementService
    ) {
        return new PopService(keyManagementService);
    }
}
