package de.adorsys.sts.common;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.common"
})
public class SecureTokenServiceConfig {
}
