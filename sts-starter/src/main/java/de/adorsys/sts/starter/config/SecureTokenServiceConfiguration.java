package de.adorsys.sts.starter.config;

import org.adorsys.encobject.service.ExtendedStoreConnection;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.keymanagement.KeyManagementConfigurationProperties;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.persistence.FsPersistenceKeyStoreRepository;
import de.adorsys.sts.persistence.KeyEntryMapper;
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

@Configuration
@EnablePOP
@EnableTokenExchange
@EnablePasswordGrant
@EnableAdmin
@EnableKeyRotation
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
    		ExtendedStoreConnection storeConnection,
            KeyManagementService keyManagementService
    ) {
        return new FsPersistenceResourceServerRepository(storeConnection, keyManagementService);
    }

    @Bean
    KeyEntryMapper keyEntryMapper(
            ObjectMapperSPI objectMapper
    ) {
        return new KeyEntryMapper(objectMapper);
    }

    @Bean
    KeyStoreRepository keyStoreRepository(
    		ExtendedStoreConnection storeConnection,
            KeyManagementConfigurationProperties keyManagementProperties,
            KeyEntryMapper keyEntryMapper
    ) {
        return new FsPersistenceKeyStoreRepository(
        		storeConnection,
                keyManagementProperties,
                keyEntryMapper
        );
    }
}
