package de.adorsys.sts.secretserver.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("Secure-Token-Service"));
    }
}
