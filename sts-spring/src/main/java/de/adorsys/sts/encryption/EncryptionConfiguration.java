package de.adorsys.sts.encryption;

import de.adorsys.sts.resourceserver.ResourceServerManagementConfiguration;
import de.adorsys.sts.resourceserver.ResourceServerManagementConfigurationProperties;
import de.adorsys.sts.resourceserver.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ResourceServerManagementConfiguration.class)
public class EncryptionConfiguration {

    @Bean
    public KeyRetrieverService keyRetrieverService(
            ResourceServerService resourceServerService,
            ResourceServerManagementConfigurationProperties resourceServerManagementConfigurationProperties,
            @Value("${sts.resource-server-management.resource-retriever.cache.enabled:false}")
                    Boolean isCacheEnabled,
            @Value("${sts.resource-server-management.resource-retriever.cache.maximum-size:1000}")
                    Integer maximumSize,
            @Value("${sts.resource-server-management.resource-retriever.cache.expire-after-access:10}")
                    Integer expireAfterAccessInMinutes
    ) {
        KeyRetrieverService keyRetrieverService = new RemoteKeyRetrieverService(
                resourceServerService,
                resourceServerManagementConfigurationProperties
        );

        if(isCacheEnabled) {
            keyRetrieverService = new CachingKeyRetrieverService(
                    keyRetrieverService,
                    maximumSize,
                    expireAfterAccessInMinutes
            );
        }

        return new LoggingKeyRetrieverService(keyRetrieverService);
    }

    @Bean
    public EncryptionService encryptionService(
            KeyRetrieverService keyRetrieverService
    ) {
        EncryptionService encryptionService = new JweEncryptionService(keyRetrieverService);
        return new LoggingEncryptionService(encryptionService);
    }
}
