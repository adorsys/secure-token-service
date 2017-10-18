package de.adorsys.sts.resourceserver.service;

import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import de.adorsys.sts.resourceserver.ResourceServerInfo;
import de.adorsys.sts.resourceserver.model.UserCredentials;
import de.adorsys.sts.resourceserver.service.UserDataService;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.resourceserver.service.SecretEncryptionException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Processes information specific to a resoruce server.
 * 
 * @author fpo
 *
 */
@Service
public class ResourceServerProcessor {

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
	
    @Autowired
    private ResourceServerService resourceServerService;

    @Autowired
	private EncryptionService encryptionService;

    private static JWKSelector encKeySelector = new JWKSelector(new JWKMatcher.Builder().keyUse(KeyUse.ENCRYPTION).build());
	private ResourceRetriever resourceRetriever=new DefaultResourceRetriever(DEFAULT_HTTP_CONNECT_TIMEOUT, DEFAULT_HTTP_READ_TIMEOUT, DEFAULT_HTTP_SIZE_LIMIT);

	/**
	 * Returns the list of resource server with corresponding user custom secret.
	 * 
	 * @param audiences
	 * @param resources
	 * @param userDataService
	 * @return
	 */
	public List<ResourceServerAndSecret> processResources(String[] audiences, String[] resources, UserDataService userDataService){
		
		// Result
		List<ResourceServerAndSecret> resurceServers = new ArrayList<>();
			
		Map<String, Map<String, ResourceServer>> resourceServersMultiMap = resourceServerService.getAll().toMultiMap();
		
		if(audiences!=null) filterServersByAudience(audiences, resourceServersMultiMap, resurceServers);

		if(resources!=null)filterServersByResources(resources, resourceServersMultiMap, resurceServers);
		
		if(resurceServers.isEmpty()) return resurceServers;
		
		// If Resources are set, we can get or create the corresponding user secrets and have them included in the token.
		loadUserCredentials(userDataService, resurceServers);

		// Encrypt credentials for token
		for (ResourceServerAndSecret resourceServerAndSecret : resurceServers) {
			encryptSecret(resourceServerAndSecret);
		}

		return resurceServers;
	}

	public List<ResourceServerAndSecret> processResources(String[] audiences, String[] resources){

		// Result
		List<ResourceServerAndSecret> resurceServers = new ArrayList<>();

		Map<String, Map<String, ResourceServer>> resourceServersMultiMap = resourceServerService.getAll().toMultiMap();

		if(audiences!=null) filterServersByAudience(audiences, resourceServersMultiMap, resurceServers);

		if(resources!=null)filterServersByResources(resources, resourceServersMultiMap, resurceServers);

		if(resurceServers.isEmpty()) return resurceServers;

		// Encrypt credentials for token
		for (ResourceServerAndSecret resourceServerAndSecret : resurceServers) {
			encryptSecret(resourceServerAndSecret);
		}

		return resurceServers;
	}

	private void encryptSecret(ResourceServerAndSecret resourceServerAndSecret) {
		ResourceServer resourceServer = resourceServerAndSecret.getResourceServer();

		if(StringUtils.isBlank(resourceServer.getUserSecretClaimName())) return;

		Optional<String> encryptedSecret = tryToEncrypt(resourceServerAndSecret);

		encryptedSecret.ifPresent(resourceServerAndSecret::setEncryptedSecret);
	}

	private Optional<String> tryToEncrypt(ResourceServerAndSecret resourceServerAndSecret) {
		Optional<String> encryptedSecret = Optional.empty();

		ResourceServer resourceServer = resourceServerAndSecret.getResourceServer();

		if(StringUtils.isBlank(resourceServer.getUserSecretClaimName())) return encryptedSecret;

		ResourceServerInfo serverInfo = new ResourceServerInfo(resourceRetriever, resourceServer);
		RemoteJWKSet<SecurityContext> jwkSource = serverInfo.getJWKSource();
		List<JWK> keys;
		try {
			keys = jwkSource.get(encKeySelector, null);
		} catch (RemoteKeySourceException e) {
			// TODO. Log Warn
			e.printStackTrace();
			return encryptedSecret;
		}
		if(keys==null ||  keys.isEmpty()) return encryptedSecret;

		JWK jwk = keys.iterator().next();

		String encrypted;
		try {
			encrypted = encryptionService.encrypt(jwk, resourceServerAndSecret.getRawSecret());
		} catch(SecretEncryptionException e) {
			// TODO log.warn
			e.printStackTrace();
			return encryptedSecret;
		}

		return Optional.of(encrypted);
	}

	private List<ResourceServerAndSecret> filterServersByResources(String[] resources, Map<String, Map<String, ResourceServer>> resourceServersMultiMap, final List<ResourceServerAndSecret> result){
		Map<String, ResourceServer> map = resourceServersMultiMap.get(ResourceServers.ENDPOINT);
		return filterServers0(resources, map, result);
	}
	private List<ResourceServerAndSecret> filterServersByAudience(String[] audiences, Map<String, Map<String, ResourceServer>> resourceServersMultiMap, final List<ResourceServerAndSecret> result){
		Map<String, ResourceServer> map = resourceServersMultiMap.get(ResourceServers.AUDIENCE);
		return filterServers0(audiences, map, result);
	}

	private List<ResourceServerAndSecret> filterServers0(String[] keys, Map<String, ResourceServer> map, final List<ResourceServerAndSecret> result){
		for (String key : keys) {
			ResourceServer resourceServer = map.get(key);
			if(resourceServer==null) continue;
			for (ResourceServerAndSecret resourceServerAndSecret : result) {
				if(resourceServer.equals(resourceServerAndSecret.getResourceServer())) continue;
			}
			ResourceServerAndSecret resourceServerAndSecret = ResourceServerAndSecret.builder()
					.resourceServer(resourceServer)
					.build();
			result.add(resourceServerAndSecret);
		}
		return result;
	}
	
	private void loadUserCredentials(UserDataService userDataService, List<ResourceServerAndSecret> resurceServers){
		if(userDataService==null) return;
		// If Resources are set, we can get or create the corresponding user secrets and have them included in the token.
		UserCredentials userCredentials = userDataService.loadUserCredentials();

		boolean store = false;
		for (ResourceServerAndSecret resourceServer : resurceServers) {
			String credentialForResourceServer = userCredentials.getCredentialForResourceServer(resourceServer.getResourceServer().getAudience());
			if(credentialForResourceServer==null){
				// create one
				credentialForResourceServer = RandomStringUtils.randomGraph(16);
				userCredentials.setCredentialForResourceServer(resourceServer.getResourceServer().getAudience(), credentialForResourceServer);
				store = true;
			}
			resourceServer.setRawSecret(credentialForResourceServer);
		}
		if(store){
			userDataService.storeUserCredentials(userCredentials);
		}
	}
	
	public void storeUserCredentials(UserDataService userDataService, String credentialForResourceServer, String resurceServerAudience){
		if(userDataService==null) return;
		// Result
		List<ResourceServerAndSecret> resurceServers = new ArrayList<>();
		Map<String, Map<String, ResourceServer>> resourceServersMultiMap = resourceServerService.getAll().toMultiMap();

		String[] resurceServerAudiences = new String[]{resurceServerAudience};
		List<ResourceServerAndSecret> filterServersByAudience = filterServersByAudience(resurceServerAudiences, resourceServersMultiMap, resurceServers);

		// If Resources are set, we can get or create the corresponding user secrets and have them included in the token.
		UserCredentials userCredentials = userDataService.loadUserCredentials();

		if(filterServersByAudience.isEmpty()) return;
		
		ResourceServerAndSecret resourceServer = filterServersByAudience.get(0);
		String oldCredentialForResourceServer = userCredentials.getCredentialForResourceServer(resourceServer.getResourceServer().getAudience());
		if(oldCredentialForResourceServer!=null) return;
		userCredentials.setCredentialForResourceServer(resourceServer.getResourceServer().getAudience(), credentialForResourceServer);
		userDataService.storeUserCredentials(userCredentials);
	}
	
}
