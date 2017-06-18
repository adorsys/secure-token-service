package de.adorsys.sts.main;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.adorsys.envutils.EnvProperties;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import de.adorsys.sts.config.ServerKeyManagerConfig;
import de.adorsys.sts.config.SwaggerConfig;
import de.adorsys.sts.config.WebSecurityConfig;
import de.adorsys.sts.info.ServerInfoController;
import de.adorsys.sts.persistence.STSPersistenceConfig;
import de.adorsys.sts.pop.PoPController;
import de.adorsys.sts.token.TokenController;
import de.adorsys.sts.token.TokenService;

@SpringBootApplication
@ComponentScan(basePackageClasses = { TokenController.class, SwaggerConfig.class, WebSecurityConfig.class,
		ServerKeyManagerConfig.class, TokenService.class, STSPersistenceConfig.class, ServerInfoController.class, PoPController.class })
public class SecureTokenServiceApplication {

	public static void main(String[] args) throws UnknownHostException {
//		turnOffEncPolicy();

		String keystorePassword = EnvProperties.getEnvOrSysProp("KEYSTORE_PASSWORD", true);
		if(StringUtils.isBlank(keystorePassword)){
			keystorePassword = RandomStringUtils.randomAlphanumeric(16);
			System.setProperty("KEYSTORE_PASSWORD", keystorePassword);
			System.setProperty("RESET_KEYSTORE", "true");
			LoggerFactory.getLogger(SecureTokenServiceApplication.class).info("Newly generated Keystore Password: " + keystorePassword );
		}
		ConfigurableApplicationContext app = SpringApplication.run(SecureTokenServiceApplication.class, args);
		Environment env = app.getEnvironment();
		String protocol = "http";
		if (env.getProperty("server.ssl.key-store") != null) {
			protocol = "https";
		}
		LoggerFactory.getLogger(SecureTokenServiceApplication.class)
				.info("\n----------------------------------------------------------\n\t"
						+ "Application '{}' is running! Access URLs:\n\t" + "Local: \t\t{}://localhost:{}\n\t"
						+ "External: \t{}://{}:{}\n\t"
						+ "Profile(s): \t{}\n----------------------------------------------------------",
						env.getProperty("spring.application.name", "Secure Token Service"), protocol,
						env.getProperty("server.port", "8080"), protocol, InetAddress.getLocalHost().getHostAddress(),
						env.getProperty("server.port", "8080"), env.getActiveProfiles());
		
	}

	public static void turnOffEncPolicy(){
		// Warning: do not do this for productive code. Download and install the jce unlimited strength policy file
		// see http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html
		try {
	        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
	        field.setAccessible(true);
	        field.set(null, java.lang.Boolean.FALSE);
	    } catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
	        ex.printStackTrace(System.err);
	    }		
	}
}
