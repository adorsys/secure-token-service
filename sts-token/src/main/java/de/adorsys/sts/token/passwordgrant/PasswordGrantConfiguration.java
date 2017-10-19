package de.adorsys.sts.token.passwordgrant;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "de.adorsys.sts.token.passwordgrant",
        "de.adorsys.sts.resourceserver.processing"
})
public class PasswordGrantConfiguration {
}
