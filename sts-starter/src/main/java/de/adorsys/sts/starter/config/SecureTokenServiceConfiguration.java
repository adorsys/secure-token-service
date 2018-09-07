package de.adorsys.sts.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.lockpersistence.client.LockClient;
import de.adorsys.lockpersistence.client.NoopLockClient;
import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.keymanagement.KeyManagementConfigurationProperties;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.keyrotation.EnableKeyRotation;
import de.adorsys.sts.persistence.FsKeyStoreRepository;
import de.adorsys.sts.persistence.FsResourceServerRepository;
import de.adorsys.sts.persistence.FsUserDataRepository;
import de.adorsys.sts.persistence.KeyEntryMapper;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import de.adorsys.sts.resourceserver.service.UserDataRepository;
import de.adorsys.sts.serverinfo.EnableServerInfo;
import de.adorsys.sts.token.passwordgrant.EnablePasswordGrant;
import de.adorsys.sts.token.tokenexchange.server.EnableTokenExchangeServer;
import de.adorsys.sts.worksheetloader.DataSheetLoader;
import de.adorsys.sts.worksheetloader.LoginLoader;
import org.adorsys.docusafe.business.DocumentSafeService;
import org.adorsys.docusafe.business.types.UserID;
import org.adorsys.docusafe.business.types.complex.UserIDAuth;
import org.adorsys.encobject.domain.ReadKeyPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@EnablePOP
@EnableTokenExchangeServer
@EnablePasswordGrant
@EnableAdmin
@EnableKeyRotation
@EnableServerInfo
public class SecureTokenServiceConfiguration {
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private DocumentSafeService documentSafeService;
	
    @Value("${docusafe.system.user.name}")
    String docusafeSystemUserName;
    @Value("${docusafe.system.user.password}")
    String docusafeSystemUserPassword;
    
    private UserIDAuth systemIdAuth;
    
    @PostConstruct
    public void postConstruct(){
    	systemIdAuth = new UserIDAuth(new UserID(docusafeSystemUserName), new ReadKeyPassword(docusafeSystemUserPassword));    	
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
    ResourceServerRepository resourceServerRepository() {
        return new FsResourceServerRepository(systemIdAuth, documentSafeService, objectMapper);
    }

    @Bean
    KeyStoreRepository keyStoreRepository(KeyManagementConfigurationProperties keyManagementProperties) {
        return new FsKeyStoreRepository(systemIdAuth, documentSafeService, keyManagementProperties, new KeyEntryMapper(objectMapper));
    }

    @Bean
    UserDataRepository userDataRepository() {
        return new FsUserDataRepository(documentSafeService, objectMapper);
    }

    @Bean
    LockClient lockClient() {
        return new NoopLockClient();
    }
}
