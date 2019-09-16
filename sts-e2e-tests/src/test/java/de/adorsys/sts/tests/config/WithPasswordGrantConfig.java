package de.adorsys.sts.tests.config;

import de.adorsys.sts.resourceserver.model.UserCredentials;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import org.springframework.context.annotation.Bean;

@EnablePasswordGrant
public class WithPasswordGrantConfig {

    @Bean
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

    @Bean
    UserDataRepository userDataRepository() {
        return new UserDataRepository() {
            @Override
            public void addAccount(String user, String password) {}

            @Override
            public boolean hasAccount(String user) {
                return false;
            }

            @Override
            public UserCredentials loadUserCredentials(String user, String password) {
                return null;
            }

            @Override
            public void storeUserCredentials(String user, String password, UserCredentials userCredentials) {}
        };
    }
}
