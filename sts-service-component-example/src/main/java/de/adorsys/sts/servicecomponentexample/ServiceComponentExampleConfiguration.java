package de.adorsys.sts.servicecomponentexample;

import de.adorsys.sts.decryption.EnableDecryption;
import de.adorsys.sts.keymanagement.EnableKeyManagement;
import de.adorsys.sts.keymanagement.persistence.InMemoryKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.authentication.EnableTokenAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTokenAuthentication
@EnablePOP
@EnableDecryption
@EnableKeyManagement
@EnableKeyRotation
public class ServiceComponentExampleConfiguration {

    @Bean
    KeyStoreRepository keyStoreRepository() {
        return new InMemoryKeyStoreRepository();
    }
}
