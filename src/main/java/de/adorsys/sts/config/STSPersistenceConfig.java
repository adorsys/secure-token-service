package de.adorsys.sts.config;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class STSPersistenceConfig {
	private static final String SERVER_PERSISTENCE_DIR = "SERVER_PERSISTENCE_DIR";
	
	private FsPersistenceFactory persFactory;
	
    @PostConstruct
    public void postConstruct() {
    	String baseDir = EnvProperties.getEnvOrSysProp(SERVER_PERSISTENCE_DIR, "./target/sts");
    	persFactory = new FsPersistenceFactory(baseDir);
    }

    @Bean
	public FsPersistenceFactory getPersFactory() {
		return persFactory;
	}
}
