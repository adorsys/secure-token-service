package de.adorsys.sts.starter.config;

import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.keymanagement.EnableKeyManagement;
import de.adorsys.sts.keymanagement.KeyManagementConfigurationProperties;
import de.adorsys.sts.keymanagement.persistence.FsPersistenceKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.resourceserver.persistence.FsPersistenceResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.provider.EnvironmentVariableResourceServersProvider;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import de.adorsys.sts.serverinfo.EnableServerInfo;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import de.adorsys.sts.token.tokenexchange.EnableTokenExchange;
import de.adorsys.sts.worksheetloader.DataSheetLoader;
import de.adorsys.sts.worksheetloader.LoginLoader;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnablePOP
@EnableTokenExchange
@EnablePasswordGrant
@EnableAdmin
@EnableKeyManagement
@EnableServerInfo
public class SecureTokenServiceConfiguration {

    @Bean
    public DataSheetLoader dataSheetLoader() {
        return new DataSheetLoader();
    }

    @Bean
    public LoginLoader loginLoader() {
        return new LoginLoader();
    }

    @Bean
    public ResourceServersProvider resourceServersProvider() {
        return new EnvironmentVariableResourceServersProvider();
    }

    @Bean
    ResourceServerRepository resourceServerRepository(
            FsPersistenceFactory fsPersistenceFactory,
            KeyManagementService keyManagementService
    ) {
        return new FsPersistenceResourceServerRepository(fsPersistenceFactory, keyManagementService);
    }

    @Bean
    KeyStoreRepository keyStoreRepository(
            FsPersistenceFactory fsPersistenceFactory,
            KeyManagementConfigurationProperties keyManagementProperties
    ) {
        return new FsPersistenceKeyStoreRepository(
                fsPersistenceFactory,
                keyManagementProperties
        );
    }
}
