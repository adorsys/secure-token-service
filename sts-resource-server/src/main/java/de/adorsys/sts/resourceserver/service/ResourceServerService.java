package de.adorsys.sts.resourceserver.service;

import com.sun.media.sound.AiffFileReader;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.persistence.ResourceServerRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    public Map<String, ResourceServer> getForAudiences(String[] audiences) {
        List<String> audiencesAsList = Arrays.asList(audiences);

        return repository.getAll()
                .stream()
                .filter(r -> audiencesAsList.contains(r.getAudience()))
                .collect(
                        Collectors.toMap(ResourceServer::getAudience, Function.identity())
                );
    }

    public Map<String, ResourceServer> getForResources(String[] resources) {
        List<String> resourcesAsList = Arrays.asList(resources);

        return repository.getAll()
                .stream()
                .filter(r -> resourcesAsList.contains(r.getEndpointUrl()))
                .collect(
                        Collectors.toMap(ResourceServer::getEndpointUrl, Function.identity())
                );
    }

    public ResourceServer getForResource(String resource) {
        Map<String, Map<String, ResourceServer>> resourceServersMap = getMappedResourceServers();
        ResourceServer resourceServer = resourceServersMap.get(ResourceServers.ENDPOINT).get(resource);

        if(resourceServer == null) {
            throw new RuntimeException("No resource server found for resource '" + resource + "'");
        }

        return resourceServer;
    }

    public List<ResourceServer> getForAudiencesAndResources(String[] audiences, String[] resources) {
        List<ResourceServer> resourceServers = repository.getAll();

        Predicate<ResourceServer> filter = new ByAudiencesOrResourcesFilter(audiences, resources);

        return resourceServers.stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    private class ByAudiencesOrResourcesFilter implements Predicate<ResourceServer> {

        private final List<String> audiences;
        private final List<String> resources;

        private ByAudiencesOrResourcesFilter(String[] audiences, String[] resources) {
            this.audiences = Arrays.asList(audiences);
            this.resources = Arrays.asList(resources);
        }

        @Override
        public boolean test(ResourceServer resourceServer) {
            return audiences.contains(resourceServer.getAudience()) || resources.contains(resourceServer.getEndpointUrl());
        }
    }

    private Map<String, Map<String, ResourceServer>> getMappedResourceServers() {
        ResourceServers resourceServers = ResourceServers.builder()
                .servers(repository.getAll())
                .build();

        return resourceServers.toMultiMap();
    }
}
