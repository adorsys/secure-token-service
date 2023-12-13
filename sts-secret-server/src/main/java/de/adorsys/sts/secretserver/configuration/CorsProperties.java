package de.adorsys.sts.secretserver.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cors")
@Data
public class CorsProperties {

    private boolean disbaled;
    private String[] allowedOrigins;
    private String allowedHeaders;
    private String[] allowedMethods;
}