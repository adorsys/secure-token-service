package de.adorsys.sts.resourceserver.initializer;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("sts")
public class ResourceServersProperties {
    private List<ResourceServer> resourceServers = new ArrayList<>();

    public List<ResourceServer> getResourceServers() {
        return resourceServers;
    }

    public void setResourceServers(List<ResourceServer> resourceServers) {
        this.resourceServers = resourceServers;
    }
}
