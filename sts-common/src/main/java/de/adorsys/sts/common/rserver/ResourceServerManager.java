package de.adorsys.sts.common.rserver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.filesystem.FsPersistenceFactory;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.encobject.service.ContainerExistsException;
import org.adorsys.encobject.service.ContainerPersistence;
import org.adorsys.encobject.service.ObjectNotFoundException;
import org.adorsys.encobject.service.UnknownContainerException;
import org.adorsys.encobject.service.WrongKeyCredentialException;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.ServerKeyManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provide standard management routines for endpoints.
 * 
 * We are interested in:
 *   - jwks_url The jwks_url of an endpoint so we can read and key published by that endpoint for authenticated encryption
 *   - pop The pop endpoint for the same purpose.
 *   
 * We can cache the thse keys and refresh them at well defined intervals using the parameter:
 *   - refresh_interval_seconds
 * 
 * @author fpo
 *
 */
@Service
public class ResourceServerManager {
	
	private static final String RESOURCE_SERVER_CONTAINER = "RESOURCE_SERVER_CONTAINER";

	@Autowired
	private FsPersistenceFactory persFactory;

    @Autowired
    private ServerKeyManager keyManager;

	private String containerName;
    private static final String RESOURCE_SERVERS_FILE_NAME = "resource_servers";
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    private ResourceServers resourceServers;
    private Map<String, Map<String, ResourceServer>> resourceServersMultiMap;
    
	
	@PostConstruct
	public void postConstruct(){
        containerName = EnvProperties.getEnvOrSysProp(RESOURCE_SERVER_CONTAINER, "sts-rservers");
    	ContainerPersistence containerPersistence = persFactory.getContainerPersistence();

        if(!containerPersistence.containerExists(containerName)){
        	try {
				containerPersistence.creteContainer(containerName);
			} catch (ContainerExistsException e) {
				throw new IllegalStateException(e);
			}
        }
        resourceServers = loadResourceServers();
        resourceServersMultiMap = resourceServers.toMultiMap();
	}

	public ResourceServers loadResourceServers(){
		ResourceServers resourceServers = new ResourceServers();
		ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
		byte[] resourceServersByte = null;
		try {
			resourceServersByte = persFactory.getServerObjectPersistence().loadObject(handle, keyManager);
		} catch (ObjectNotFoundException e) {
			// No list stored sofar
			return resourceServers;
		} catch (WrongKeyCredentialException | UnknownContainerException e) {
			throw new IllegalStateException(e);
		}
		try {
			resourceServers = objectMapper.readValue(resourceServersByte, ResourceServers.class);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return resourceServers;
	}
	
	public Map<String, Map<String, ResourceServer>> getResourceServersMultiMap() {
		return resourceServersMultiMap;
	}


	public ResourceServers addResourceServers(ResourceServers resourceServersIn) throws ResourceServerException{
		List<ResourceServer> serversIn = resourceServersIn.getServers();
		ResourceServers resourceServers = loadResourceServers();
		
		if(serversIn.isEmpty()){
			return resourceServers;
		}
		
		ResourceServerErrors errors = new ResourceServerErrors();
		List<ResourceServer> serversList = resourceServers.getServers();
		for (ResourceServer resourceServer : serversIn) {
			if(StringUtils.isBlank(resourceServer.getAudience())){
				ResourceServerError resourceServerError = new ResourceServerError("missing audience", resourceServer);
				errors.getErros().add(resourceServerError);
				continue;
			}

			ResourceServer serverToReplace = null;
			for (ResourceServer server : serversList) {
				if(resourceServer.equals(server)) continue;
				if(StringUtils.equals(resourceServer.getAudience(), server.getAudience())){
					serverToReplace = server;
					break;
				}
			}
			if(serverToReplace!=null){
				int indexOf = serversList.indexOf(serverToReplace);
				serversList.set(indexOf, resourceServer);
			}
		}
		if(!errors.getErros().isEmpty()){
			throw new ResourceServerException(errors);
		}
		
		for (ResourceServer resourceServer : serversIn) {
			resourceServers.getServers().add(resourceServer);
			byte[] data;
			try {
				data = objectMapper.writeValueAsBytes(resourceServers);
			} catch (JsonProcessingException e) {
				throw new IllegalStateException(e);
			}
			ContentMetaInfo metaIno = null;
			ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
			EncryptionParams encParams = null;
			KeyAndJwk randomSecretKey = keyManager.getKeyMap().randomSecretKey();
			try {
				persFactory.getServerObjectPersistence().storeObject(data, metaIno, handle, keyManager, randomSecretKey.jwk.getKeyID(), encParams);
			} catch (UnsupportedEncAlgorithmException | UnsupportedKeyLengthException | UnknownContainerException e) {
				throw new IllegalStateException(e);
			}
		}
		this.resourceServers = resourceServers;
        this.resourceServersMultiMap = resourceServers.toMultiMap();
		return resourceServers;
	}

}
