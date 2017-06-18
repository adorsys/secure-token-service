package de.adorsys.sts.persistence;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.service.BlobStoreContextFactory;
import org.adorsys.encobject.service.ContainerPersistence;
import org.adorsys.encobject.service.KeystorePersistence;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class STSPersistenceConfig {

    private KeystorePersistence keystorePersistence;
    private FsBlobStoreFactory fsBlobStoreFactory; 
    private ContainerPersistence containerPersistence;
    private DirectKeyObjectPersistence objectPersistence;

    @PostConstruct
    public void initBean() {
    	String baseDir = EnvProperties.getEnvOrSysProp("SERVER_PERSISTENCE_DIR", "./target/secure-token-service");
    	FsBlobStoreFactory fsBlobStoreFactory = new FsBlobStoreFactory(baseDir);
    	keystorePersistence = new KeystorePersistence(fsBlobStoreFactory);
    	containerPersistence = new ContainerPersistence(fsBlobStoreFactory);
    	objectPersistence = new DirectKeyObjectPersistence(fsBlobStoreFactory);
    }

    @Bean
    public BlobStoreContextFactory getBlobStoreContextFactory() {
        return fsBlobStoreFactory;
    }

    @Bean
    public KeystorePersistence getKeystorePersistence() {
        return keystorePersistence;
    }
    
    @Bean
    public ContainerPersistence getContainerPersistence() {
        return containerPersistence;
    }

    @Bean
    public DirectKeyObjectPersistence getObjectPersistence() {
        return objectPersistence;
    }
}
