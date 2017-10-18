package de.adorsys.sts.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.sts.resourceserver.EnableResourceServerManagement;
import de.adorsys.sts.resourceserver.initializer.EnableResourceServerInitialization;
import de.adorsys.sts.resourceserver.persistence.InMemoryResourceServerRepository;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.serverdata.JcloudConstants;
import org.adorsys.encobject.userdata.ObjectMapperSPI;
import org.adorsys.encobject.userdata.UserDataNamingPolicy;
import org.adorsys.envutils.EnvProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@EnableResourceServerInitialization
@EnableResourceServerManagement
public class ExampleConfiguration {

    private static final String APP_NAME = "sts-example";

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
    ResourceServerRepository resourceServerRepository() {
        return new InMemoryResourceServerRepository();
    }

    @Bean
    ObjectMapperSPI objectMapper() {
        return new ObjectMapperSPI() {
            private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

            @Override
            public <T> T readValue(byte[] src, Class<T> klass) throws IOException {
                return objectMapper.readValue(src, klass);
            }

            @Override
            public <T> byte[] writeValueAsBytes(T t) throws IOException {
                return objectMapper.writeValueAsBytes(t);
            }
        };
    }
}
