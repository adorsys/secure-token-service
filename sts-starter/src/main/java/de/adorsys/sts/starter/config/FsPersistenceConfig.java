package de.adorsys.sts.starter.config;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.filesystem.FileSystemExtendedStorageConnection;
import org.adorsys.encobject.service.api.ExtendedStoreConnection;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class FsPersistenceConfig {
	private static final String APP_NAME = "sts";

	private ExtendedStoreConnection storeConnection;

	@PostConstruct
	public void postConstruct() {
		String baseDir = EnvProperties.getEnvOrSysProp("JCLOUD_FS_PERSISTENCE_DIR", "./target/"+APP_NAME);
		storeConnection = new FileSystemExtendedStorageConnection(baseDir);
	}

    @Bean
	public ExtendedStoreConnection geExtendedStorageConnection() {
		return storeConnection;
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
