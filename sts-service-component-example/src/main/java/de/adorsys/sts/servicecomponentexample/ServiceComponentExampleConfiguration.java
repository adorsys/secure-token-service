package de.adorsys.sts.servicecomponentexample;

import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.decryption.secret.EnableSecretDecryption;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.objectmapper.EnableJacksonObjectMapping;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.authentication.EnableTokenAuthentication;
import de.adorsys.sts.token.authentication.securitycontext.EnableSecurityContextSecretProviding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTokenAuthentication
@EnablePOP
@EnableDecryption
@EnableKeyRotation
@EnableSecretDecryption
@EnableSecurityContextSecretProviding
@EnableJacksonObjectMapping
public class ServiceComponentExampleConfiguration {
}
