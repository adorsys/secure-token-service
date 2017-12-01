package de.adorsys.sts.admin;

import de.adorsys.sts.resourceserver.ResourceServerManagementConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.admin"
})
@Import(ResourceServerManagementConfiguration.class)
public class AdminConfiguration {
}
