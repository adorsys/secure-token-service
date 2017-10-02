package de.adorsys.sts.resourceserver.provider;

import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.util.List;

public interface ResourceServersProvider {

    List<ResourceServer> get();
}
