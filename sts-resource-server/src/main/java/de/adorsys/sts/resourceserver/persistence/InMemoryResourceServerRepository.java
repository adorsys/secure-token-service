package de.adorsys.sts.resourceserver.persistence;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.util.List;

public class InMemoryResourceServerRepository implements ResourceServerRepository {

    private final List<ResourceServer> resourceServers = Lists.newArrayList();

    @Override
    public List<ResourceServer> getAll() {
        return ImmutableList.<ResourceServer>builder()
                .addAll(resourceServers)
                .build();
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
