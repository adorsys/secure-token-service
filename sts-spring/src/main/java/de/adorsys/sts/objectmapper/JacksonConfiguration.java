package de.adorsys.sts.objectmapper;

import de.adorsys.sts.common.ObjectMapperSPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    ObjectMapperSPI objectMapper() {
        return new JacksonObjectMapper();
    }
}
