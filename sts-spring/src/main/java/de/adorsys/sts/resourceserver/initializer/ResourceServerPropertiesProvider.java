package de.adorsys.sts.resourceserver.initializer;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import de.adorsys.sts.resourceserver.service.ResourceServerManagementProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResourceServerPropertiesProvider implements ResourceServersProvider {

    private final ResourceServerManagementProperties properties;

    @Autowired
    public ResourceServerPropertiesProvider(ResourceServerManagementProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ResourceServer> get() {
        return properties.getResourceServers();
    }
}
