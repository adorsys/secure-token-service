package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ResourceServerService {

    private final ResourceServerRepository repository;

    @Autowired
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

    public ResourceServer getForClient(String clientId) {
        Map<String, Map<String, ResourceServer>> resourceServersMap = getMappedResourceServers();
        ResourceServer resourceServer = resourceServersMap.get(ResourceServers.CLIENT_ID).get(clientId);

        if(resourceServer == null) {
            throw new RuntimeException("No resource server found for client '" + clientId + "'");
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
