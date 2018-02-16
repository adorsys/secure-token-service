package de.adorsys.sts.resourceserver.service;

import de.adorsys.sts.resourceserver.model.ResourceServer;

import java.util.List;

public interface ResourceServerManagementProperties {

    List<ResourceServer> getResourceServers();
    ResourceRetrieverProperties getResourceRetriever();

    interface ResourceRetrieverProperties {
        Integer getHttpConnectTimeout();
        Integer getHttpReadTimeout();
        Integer getHttpSizeLimit();
    }
}
