package de.adorsys.sts.example.config;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties("app")
public class ResourceServersProperties {
    private List<ResourceServer> servers = new ArrayList<>();

    public List<ResourceServer> getServers() {
        return servers;
    }

    public void setServers(List<ResourceServer> servers) {
        this.servers = servers;
    }
}
