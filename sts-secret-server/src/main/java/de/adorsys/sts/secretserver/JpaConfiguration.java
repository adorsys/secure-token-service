package de.adorsys.sts.secretserver;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableJpaPersistence
@Profile({"!mongo"})
public class JpaConfiguration {
}
