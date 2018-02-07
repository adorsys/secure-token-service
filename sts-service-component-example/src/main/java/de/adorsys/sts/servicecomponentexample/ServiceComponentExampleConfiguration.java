package de.adorsys.sts.servicecomponentexample;

import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.authentication.EnableTokenAuthentication;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTokenAuthentication
@EnablePOP
@EnableDecryption
@EnableKeyRotation
@EnableJpaPersistence
public class ServiceComponentExampleConfiguration {
}
