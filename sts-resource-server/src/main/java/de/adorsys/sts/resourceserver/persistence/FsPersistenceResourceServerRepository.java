package de.adorsys.sts.resourceserver.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.encobject.service.*;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.ServerKeyMapProvider;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FsPersistenceResourceServerRepository implements ResourceServerRepository {
    private static final TypeReference<List<ResourceServer>> RESOURCE_SERVER_LIST_TYPE = new TypeReference<List<ResourceServer>>() {
    };
    private static final String RESOURCE_SERVER_CONTAINER = "RESOURCE_SERVER_CONTAINER";
    private static final String RESOURCE_SERVERS_FILE_NAME = "resource_servers";

    private final FsPersistenceFactory persFactory;
    private final ServerKeyMapProvider keyMapProvider;

    private String containerName;


    private ObjectMapper objectMapper = new ObjectMapper();

    public FsPersistenceResourceServerRepository(FsPersistenceFactory persFactory, ServerKeyMapProvider keyMapProvider) {
        this.persFactory = persFactory;
        this.keyMapProvider = keyMapProvider;
    }

    @PostConstruct
    public void postConstruct() {
        containerName = EnvProperties.getEnvOrSysProp(RESOURCE_SERVER_CONTAINER, "sts-rservers");
        ContainerPersistence containerPersistence = persFactory.getContainerPersistence();

        if (!containerPersistence.containerExists(containerName)) {
            try {
                containerPersistence.creteContainer(containerName);
            } catch (ContainerExistsException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    @Override
    public List<ResourceServer> getAll() {
        return loadAll();
    }

    private List<ResourceServer> loadAll() {
        List<ResourceServer> resourceServers = Lists.newArrayList();

        ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
        byte[] resourceServersByte = null;
        try {
            resourceServersByte = persFactory.getServerObjectPersistence().loadObject(handle, keyMapProvider);
        } catch (ObjectNotFoundException e) {
            // No list stored so far
            return resourceServers;
        } catch (WrongKeyCredentialException | UnknownContainerException e) {
            throw new IllegalStateException(e);
        }
        try {
            return objectMapper.readValue(resourceServersByte, RESOURCE_SERVER_LIST_TYPE);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void add(ResourceServer resourceServer) {
        if(!isValid(resourceServer)) {
            throw new RuntimeException("Resource server not valid");
        }

        List<ResourceServer> resourceServers = loadAll();

        add(resourceServer, resourceServers);
    }

    private Map<String, ResourceServer> mapResourceServers(List<ResourceServer> resourceServers) {
        return resourceServers.stream().collect(Collectors.toMap(ResourceServer::getAudience, Function.identity()));
    }

    private void add(ResourceServer resourceServer, List<ResourceServer> existingServers) {
        Map<String, ResourceServer> resourceServerMap = mapResourceServers(existingServers);

        String audience = resourceServer.getAudience();
        if(resourceServerMap.containsKey(audience)) {
            ResourceServer existingResourceServer = resourceServerMap.get(audience);

            if(!existingResourceServer.equals(resourceServer)) {
                int indexOf = existingServers.indexOf(existingResourceServer);
                existingServers.set(indexOf, resourceServer);
            }
        } else {
            existingServers.add(resourceServer);
        }

        persist(existingServers);
    }

    private void persist(List<ResourceServer> existingServers) {
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(existingServers);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        ContentMetaInfo metaInfo = null;
        ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
        EncryptionParams encParams = null;
        KeyAndJwk randomSecretKey = keyMapProvider.getKeyMap().randomSecretKey();
        try {
            persFactory.getServerObjectPersistence().storeObject(data, metaInfo, handle, keyMapProvider, randomSecretKey.jwk.getKeyID(), encParams);
        } catch (UnsupportedEncAlgorithmException | UnsupportedKeyLengthException | UnknownContainerException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void addAll(Iterable<ResourceServer> serversIn) {
        List<ResourceServer> existingServers = loadAll();

        if (Iterables.isEmpty(serversIn)) {
            return;
        }

        for (ResourceServer resourceServer : serversIn) {
            add(resourceServer, existingServers);
        }
    }

    private boolean isValid(ResourceServer resourceServer) {
        return !StringUtils.isBlank(resourceServer.getAudience());
    }
}
