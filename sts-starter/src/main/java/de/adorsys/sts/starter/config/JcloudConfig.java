package de.adorsys.sts.starter.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.serverdata.JcloudConstants;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

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
		return new UserDataNamingPolicy(APP_NAME);
	}
	
    @Bean
	public FsPersistenceFactory getPersFactory() {
		return persFactory;
	}

	@Bean
	ObjectMapperSPI objectMapper() {
		return new ObjectMapperSPI() {
			private final TypeReference<Map<String, String>> STRING_MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
			};

			private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

			@Override
			public <T> T readValue(byte[] src, Class<T> klass) throws IOException {
				return objectMapper.readValue(src, klass);
			}

			@Override
			public <T> T readValue(String s, Class<T> klass) throws IOException {
				return objectMapper.readValue(s, klass);
			}

			@Override
			public Map<String, String> readValue(String s) throws IOException {
				return objectMapper.readValue(s, STRING_MAP_TYPE_REFERENCE);
			}

			@Override
			public <T> byte[] writeValueAsBytes(T t) throws IOException {
				return objectMapper.writeValueAsBytes(t);
			}

			@Override
			public <T> String writeValueAsString(T t) throws IOException {
				return objectMapper.writeValueAsString(t);
			}
		};
	}
}
