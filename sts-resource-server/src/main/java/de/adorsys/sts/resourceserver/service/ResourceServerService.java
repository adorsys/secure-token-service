package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.provider.ResourceServersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ResourceServerService {

    private final ResourceServersProvider resourceServersProvider;

    private ResourceServers resourceServers;
    private Map<String, Map<String, ResourceServer>> mappedResourceServers;

    @Autowired
    public ResourceServerService(ResourceServersProvider resourceServersProvider) {
        this.resourceServersProvider = resourceServersProvider;
    }

    public ResourceServer getForClient(String clientId) {
        Map<String, Map<String, ResourceServer>> resourceServersMap = getMappedResourceServers();
        ResourceServer resourceServer = resourceServersMap.get(ResourceServers.CLIENT_ID).get(clientId);

        if(resourceServer == null) {
            throw new RuntimeException("No resource server found for client '" + clientId + "'");
        }

        return resourceServer;
    }

    private Map<String, Map<String, ResourceServer>> getMappedResourceServers() {
        if(mappedResourceServers == null) {
            ResourceServers resourceServers = getResourceServers();
            mappedResourceServers = resourceServers.toMultiMap();
        }

        return mappedResourceServers;
    }

    private ResourceServers getResourceServers() {
        if(resourceServers == null) {
            resourceServers = resourceServersProvider.get();
        }

        return resourceServers;
    }
}
