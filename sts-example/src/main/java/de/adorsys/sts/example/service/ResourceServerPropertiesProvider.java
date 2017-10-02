package de.adorsys.sts.example.service;

import de.adorsys.sts.example.config.ResourceServersProperties;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties("app")
public class ResourceServerPropertiesProvider implements ResourceServersProvider {

    private final ResourceServersProperties properties;

    @Autowired
    public ResourceServerPropertiesProvider(ResourceServersProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ResourceServer> get() {
        return properties.getServers();
    }
}
