package de.adorsys.sts.pop;

import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.serverdata.JcloudConstants;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class JcloudConfig {
	private static final String APP_NAME = "sts";

	private FsPersistenceFactory persFactory;

	@PostConstruct
	public void postConstruct() {
		String baseDir = EnvProperties.getEnvOrSysProp(JcloudConstants.JCLOUD_FS_PERSISTENCE_DIR, "./target/"+APP_NAME);
		persFactory = new FsPersistenceFactory(baseDir);
	}

	@Bean
	public UserDataNamingPolicy userDataNamingPolicy(){
		UserDataNamingPolicy userDataNamingPolicy = new UserDataNamingPolicy(APP_NAME);
		return userDataNamingPolicy;
	}
	
    @Bean
	public FsPersistenceFactory getPersFactory() {
		return persFactory;
	}
}
