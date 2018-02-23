package de.adorsys.sts.resourceserver.persistence;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.complextypes.BucketPath;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.domain.Payload;
import org.adorsys.encobject.keysource.KeyMapProviderBasedKeySource;
import org.adorsys.encobject.keysource.KeySource;
import org.adorsys.encobject.service.EncryptedPersistenceService;
import org.adorsys.encobject.service.ExtendedStoreConnection;
import org.adorsys.encobject.service.JWEncryptionService;
import org.adorsys.encobject.service.SimplePayloadImpl;
import org.adorsys.encobject.types.KeyID;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.serverkey.ServerKeyMapProvider;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.sts.resourceserver.model.ResourceServer;

public class FsPersistenceResourceServerRepository implements ResourceServerRepository {
    private static final TypeReference<List<ResourceServer>> RESOURCE_SERVER_LIST_TYPE = new TypeReference<List<ResourceServer>>() {
    };
    private static final String RESOURCE_SERVER_CONTAINER = "RESOURCE_SERVER_CONTAINER";
    private static final String RESOURCE_SERVERS_FILE_NAME = "resource_servers";

    private final ExtendedStoreConnection storeConnection;
    private final EncryptedPersistenceService encryptedPersistenceService;
    private final ServerKeyMapProvider keyMapProvider;
    private KeySource keySource;

    private String containerName;


    private ObjectMapper objectMapper = new ObjectMapper();

    public FsPersistenceResourceServerRepository(ExtendedStoreConnection storeConnection, 
    		ServerKeyMapProvider keyMapProvider) {
        this.storeConnection = storeConnection;
        this.keyMapProvider = keyMapProvider;
        this.keySource = new KeyMapProviderBasedKeySource(keyMapProvider);
        this.encryptedPersistenceService = new EncryptedPersistenceService(this.storeConnection, new JWEncryptionService());
    }

    @PostConstruct
    public void postConstruct() {
        containerName = EnvProperties.getEnvOrSysProp(RESOURCE_SERVER_CONTAINER, "sts-rservers");
        if (!storeConnection.containerExists(containerName)) {
        	storeConnection.createContainer(containerName);
        }
    }

    @Override
    public List<ResourceServer> getAll() {
        return loadAll();
    }

    private List<ResourceServer> loadAll() {
        ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
        BucketPath bucketPath = BucketPath.fromHandle(handle);
        if(!storeConnection.blobExists(bucketPath)){
        	return Collections.emptyList();
        }
        Payload payload = encryptedPersistenceService.loadAndDecrypt(bucketPath, keySource);

        try {
        	return objectMapper.readValue(payload.getData(), RESOURCE_SERVER_LIST_TYPE);
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

    private void add(ResourceServer resourceServer, final List<ResourceServer> existingServers) {
        addInternal(resourceServer, existingServers);
        persist(existingServers);
    }
    
    // Add without persisting.
    private void addInternal(ResourceServer resourceServer, final List<ResourceServer> existingServers) {
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
    }
    
    private void persist(List<ResourceServer> existingServers) {
        byte[] data;
        try {
            data = objectMapper.writeValueAsBytes(existingServers);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
        BucketPath bucketPath = BucketPath.fromHandle(handle);
        KeyID keyID = new KeyID(keyMapProvider.randomSecretKey().jwk.getKeyID());
		encryptedPersistenceService.encryptAndPersist(bucketPath, new SimplePayloadImpl(data), keySource, keyID);
    }

    @Override
    public void addAll(Iterable<ResourceServer> serversIn) {
        List<ResourceServer> existingServers = loadAll();
        boolean persist = false;
        for (ResourceServer resourceServer : serversIn) {
            add(resourceServer, existingServers);
            persist = true;
        }
        if(persist)persist(existingServers);
    }

    private boolean isValid(ResourceServer resourceServer) {
        return !StringUtils.isBlank(resourceServer.getAudience());
    }
}
