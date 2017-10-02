package de.adorsys.sts.resourceserver.persistence;

import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.util.List;

public interface ResourceServerRepository {

    List<ResourceServer> getAll();
    void add(ResourceServer resourceServer);
    void addAll(Iterable<ResourceServer> resourceServers);
}
