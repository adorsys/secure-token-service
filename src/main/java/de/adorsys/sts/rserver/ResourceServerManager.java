package de.adorsys.sts.rserver;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.adorsys.encobject.domain.ContentMetaInfo;
import org.adorsys.encobject.domain.ObjectHandle;
import org.adorsys.encobject.params.EncryptionParams;
import org.adorsys.encobject.service.ContainerExistsException;
import org.adorsys.encobject.service.ContainerPersistence;
import org.adorsys.encobject.service.ObjectNotFoundException;
import org.adorsys.encobject.service.UnknownContainerException;
import org.adorsys.encobject.service.WrongKeyCredentialException;
import org.adorsys.envutils.EnvProperties;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;

import de.adorsys.sts.keystore.ServerKeyManager;
import de.adorsys.sts.keystore.ServerKeyMap.KeyAndJwk;
import de.adorsys.sts.persistence.DirectKeyObjectPersistence;

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
	
	@Autowired
	private DirectKeyObjectPersistence objectPersistence;	

	@Autowired
	private ContainerPersistence containerPersistence;
	
    @Autowired
    private ServerKeyManager keyManager;
    	
	/**
	 * The default HTTP connect timeout for JWK set retrieval, in
	 * milliseconds. Set to 250 milliseconds.
	 */
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 250;


	/**
	 * The default HTTP read timeout for JWK set retrieval, in
	 * milliseconds. Set to 250 milliseconds.
	 */
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 250;


	/**
	 * The default HTTP entity size limit for JWK set retrieval, in bytes.
	 * Set to 50 KBytes.
	 */
	public static final int DEFAULT_HTTP_SIZE_LIMIT = 50 * 1024;
	
	// Todo put this is a fifo map
	private Map<String, ResourceServerInfo> resourceServerMap = new HashMap<>();
	
	private String containerName;
    private static final String RESOURCE_SERVERS_FILE_NAME = "resource_servers";
    
    private ObjectMapper objectMapper = new ObjectMapper();
	
	@PostConstruct
	public void postConstruct(){
        containerName = EnvProperties.getEnvOrSysProp("SERVER_KEYSTORE_CONTAINER", "secure-token-service-resourceservers");
        if(!containerPersistence.containerExists(containerName)){
        	try {
				containerPersistence.creteContainer(containerName);
			} catch (ContainerExistsException e) {
				throw new IllegalStateException(e);
			}
        }
		
	}
	
	public ResourceServerInfo addResouceServer(URL metadataURL){
		if(metadataURL==null) return null;
		
		String key = metadataURL.toString();
		if(resourceServerMap.containsKey(key)) return resourceServerMap.get(key);

		// Create one
		ResourceServerInfo resourceServer = makeResourceServerInfo(metadataURL, null);
		resourceServerMap.put(key, resourceServer);
		return resourceServer;
	}

	public ResourceServerInfo addResouceServer(URL metadataURL, URL jwksURL){
		if(metadataURL==null) return null;
		
		String key = metadataURL.toString();
		if(resourceServerMap.containsKey(key)) return resourceServerMap.get(key);
		
		// Create one
		ResourceServerInfo resourceServer = makeResourceServerInfo(metadataURL, jwksURL);
		resourceServerMap.put(key, resourceServer);
		return resourceServer;
	}
	
	private ResourceServerInfo makeResourceServerInfo(URL metadataURL, URL jwksURL){
		ResourceRetriever resourceRetriever=new DefaultResourceRetriever(DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT, DEFAULT_HTTP_SIZE_LIMIT);
		return new ResourceServerInfo(resourceRetriever, metadataURL, jwksURL);
	}
	
	public ResourceServerInfo getResourceServerInfo(URL metadataURL){
		if(metadataURL==null) return null;
		String key = metadataURL.toString();
		return resourceServerMap.get(key);
	}
		
	public ResourceServerInfo getResourceServer(String metadataURL){
		if(metadataURL==null) return null;
		return resourceServerMap.get(metadataURL);
	}
	
	public ResourceServers loadResourceServers(){
		ResourceServers resourceServers = new ResourceServers();
		ObjectHandle handle = new ObjectHandle(containerName, RESOURCE_SERVERS_FILE_NAME);
		byte[] resourceServersByte = null;
		try {
			resourceServersByte = objectPersistence.loadObject(handle, keyManager);
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
	
	public ResourceServers addResourceServers(ResourceServers resourceServersIn) throws ResourceServerException{
		List<ResourceServer> serversIn = resourceServersIn.getServers();
		ResourceServers resourceServers = loadResourceServers();
		
		if(serversIn.isEmpty()){
			return resourceServers;
		}
		
		ResourceServerErrors errors = new ResourceServerErrors();
		List<ResourceServer> serversList = resourceServers.getServers();
		for (ResourceServer resourceServer : serversIn) {
			if(resourceServer.getClientId()==null) resourceServer.setClientId(UUID.randomUUID().toString());
			
			if(resourceServer.getEndpointUrl()==null){
				ResourceServerError resourceServerError = new ResourceServerError("missing end point url", resourceServer);
				errors.getErros().add(resourceServerError);
				continue;
			}

			ResourceServerError resourceServerError = null;
			for (ResourceServer server : serversList) {
				if(StringUtils.equals(resourceServer.getClientId(), server.getClientId())){
					resourceServerError = new ResourceServerError("Server with client_id " + resourceServer.getClientId() + " exists", resourceServer);
					break;
				}
				if(StringUtils.equals(resourceServer.getEndpointUrl(), server.getEndpointUrl())){
					resourceServerError = new ResourceServerError("Server with endpoint url " + resourceServer.getEndpointUrl() + " exists", resourceServer);
					break;
				}
			}
			if(resourceServerError!=null){
				errors.getErros().add(resourceServerError);
				continue;
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
				objectPersistence.storeObject(data, metaIno, handle, keyManager, randomSecretKey.jwk.getKeyID(), encParams);
			} catch (UnsupportedEncAlgorithmException | UnsupportedKeyLengthException | UnknownContainerException e) {
				throw new IllegalStateException(e);
			}
		}
		return resourceServers;
		
	}

}
