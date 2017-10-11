package de.adorsys.sts.starter.config;

import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.keymanagement.EnableKeyManagement;
import de.adorsys.sts.keymanagement.persistence.InMemoryKeyStoreRepository;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.resourceserver.EnableResourceServerManagement;
import de.adorsys.sts.resourceserver.ResourceServerProcessor;
import de.adorsys.sts.resourceserver.persistence.FsPersistenceResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.provider.EnvironmentVariableResourceServersProvider;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.serverinfo.EnableServerInfo;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import de.adorsys.sts.token.tokenexchange.EnableTokenExchange;
import de.adorsys.sts.worksheetloader.DataSheetLoader;
import de.adorsys.sts.worksheetloader.LoginLoader;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.jjwk.serverkey.ServerKeyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnablePOP
@EnableTokenExchange
@EnablePasswordGrant
@EnableAdmin
@EnableResourceServerManagement
@EnableKeyManagement
@EnableServerInfo
public class SecureTokenServiceConfiguration {

    @Bean
    public ResourceServerProcessor resourceServerProcessor() {
        return new ResourceServerProcessor();
    }

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
            ServerKeyManager serverKeyManager
    ) {
        return new FsPersistenceResourceServerRepository(fsPersistenceFactory, serverKeyManager);
    }

    @Bean
    ResourceServerService resourceServerService(
            ResourceServerRepository resourceServerRepository
    ) {
        return new ResourceServerService(resourceServerRepository);
    }

    @Bean
    KeyRetrieverService keyRetrieverService(
            ResourceServerService resourceServerService
    ) {
        return new KeyRetrieverService(resourceServerService);
    }

    @Bean
    EncryptionService encryptionService(
            KeyRetrieverService keyRetrieverService
    ) {
        return new EncryptionService(keyRetrieverService);
    }

    @Bean
    KeyStoreRepository keyStoreRepository() {
        return new InMemoryKeyStoreRepository();
    }
}
