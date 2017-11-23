package de.adorsys.sts.resourceserver.persistence;

import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InMemoryResourceServerRepository implements ResourceServerRepository {

    private final List<ResourceServer> resourceServers = new ArrayList<>();

    @Override
    public List<ResourceServer> getAll() {
        return Collections.unmodifiableList(resourceServers);
    }

    @Override
    public void add(ResourceServer resourceServer) {
        resourceServers.add(resourceServer);
    }

    @Override
    public void addAll(Iterable<ResourceServer> resourceServers) {
        for(ResourceServer resourceServer : resourceServers) {
            this.resourceServers.add(resourceServer);
        }
    }
}
