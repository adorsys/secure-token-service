package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;

import java.util.List;
import java.util.Map;

public class ResourceServerService {

    private final ResourceServerRepository repository;

    public ResourceServerService(ResourceServerRepository repository) {
        this.repository = repository;
    }

    public ResourceServers getAll() {
        List<ResourceServer> all = repository.getAll();

        return ResourceServers.builder()
                .servers(all)
                .build();
    }

    public void create(ResourceServer resourceServer) {
        repository.add(resourceServer);
    }

    public ResourceServer getForAudience(String audience) {
        Map<String, Map<String, ResourceServer>> resourceServersMap = getMappedResourceServers();
        ResourceServer resourceServer = resourceServersMap.get(ResourceServers.AUDIENCE).get(audience);

        if(resourceServer == null) {
            throw new RuntimeException("No resource server found for audience '" + audience + "'");
        }

        return resourceServer;
    }

    private Map<String, Map<String, ResourceServer>> getMappedResourceServers() {
        ResourceServers resourceServers = ResourceServers.builder()
                .servers(repository.getAll())
                .build();

        return resourceServers.toMultiMap();
    }
}
