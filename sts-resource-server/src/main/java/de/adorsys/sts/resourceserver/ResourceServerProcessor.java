package de.adorsys.sts.resourceserver;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.common.user.UserCredentials;
import de.adorsys.sts.common.user.UserDataService;
import org.adorsys.jjwk.selector.JWEEncryptedSelector;
import org.adorsys.jjwk.selector.KeyExtractionException;
import org.adorsys.jjwk.selector.UnsupportedEncAlgorithmException;
import org.adorsys.jjwk.selector.UnsupportedKeyLengthException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private ResourceServerManager resourceServerManager;
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
			
		Map<String, Map<String, ResourceServer>> resourceServersMultiMap = resourceServerManager.getResourceServersMultiMap();
		
		if(audiences!=null) filterServersByAudience(audiences, resourceServersMultiMap, resurceServers);

		if(resources!=null)filterServersByResources(resources, resourceServersMultiMap, resurceServers);
		
		if(resurceServers.isEmpty()) return resurceServers;
		
		// If Resources are set, we can get or create the corresponding user secrets and have them included in the token.
		loadUserCredentials(userDataService, resurceServers);

		// Encrypt credentials for token
		for (ResourceServerAndSecret resourceServerAndSecret : resurceServers) {
			if(StringUtils.isBlank(resourceServerAndSecret.getResourceServer().getUserSecretClaimName())) continue;
			ResourceServerInfo serverInfo = new ResourceServerInfo(resourceRetriever, resourceServerAndSecret.getResourceServer());
			RemoteJWKSet<SecurityContext> jwkSource = serverInfo.getJWKSource();
			List<JWK> keys;
			try {
				keys = jwkSource.get(encKeySelector, null);
			} catch (RemoteKeySourceException e) {
				// TODO. Log Warn
				e.printStackTrace();
				continue;
			}
			if(keys==null ||  keys.isEmpty()) continue;
			JWK jwk = keys.iterator().next();
			JWEEncrypter jweEncrypter;
			try {
				jweEncrypter = JWEEncryptedSelector.geEncrypter(jwk, null, null);
			} catch (UnsupportedEncAlgorithmException | KeyExtractionException | UnsupportedKeyLengthException e) {
				// TODO log.warn
				e.printStackTrace();
				continue;
			}
			Payload payload = new Payload(resourceServerAndSecret.getRawSecret());
			// JWE encrypt secret.
			JWEObject jweObj;
			try {
				jweObj = new JWEObject(getHeader(jwk), payload);
				jweObj.encrypt(jweEncrypter);
			} catch (JOSEException e) {
				// TODO log.warn
				e.printStackTrace();
				continue;
			}
			String serializedCredential = jweObj.serialize();
			resourceServerAndSecret.setEncryptedSecret(serializedCredential);
		}
		return resurceServers;
	}

	private JWEHeader getHeader(JWK jwk) throws JOSEException {
		JWEHeader header = null;
        if (jwk instanceof RSAKey) {
        	header = new JWEHeader(JWEAlgorithm.RSA_OAEP, EncryptionMethod.A128GCM);
        } else if (jwk instanceof ECKey) {
        	header = new JWEHeader(JWEAlgorithm.ECDH_ES_A128KW, EncryptionMethod.A192GCM);
        } else {
        	return null;
        }
        return new JWEHeader.Builder(header).keyID(jwk.getKeyID()).build();
    }
	
	private List<ResourceServerAndSecret> filterServersByResources(String[] resources, Map<String, Map<String, ResourceServer>> resourceServersMultiMap, final List<ResourceServerAndSecret> result){
		Map<String, ResourceServer> map = resourceServersMultiMap.get(ResourceServers.ENDPOINT);
		return filterServers0(resources, map, result);
	}
	private List<ResourceServerAndSecret> filterServersByAudience(String[] audiences, Map<String, Map<String, ResourceServer>> resourceServersMultiMap, final List<ResourceServerAndSecret> result){
		Map<String, ResourceServer> map = resourceServersMultiMap.get(ResourceServers.AUNDIENCE);
		return filterServers0(audiences, map, result);
	}

	private List<ResourceServerAndSecret> filterServers0(String[] keys, Map<String, ResourceServer> map, final List<ResourceServerAndSecret> result){
		for (String key : keys) {
			ResourceServer resourceServer = map.get(key);
			if(resourceServer==null) continue;
			for (ResourceServerAndSecret resourceServerAndSecret : result) {
				if(resourceServer.equals(resourceServerAndSecret.getResourceServer())) continue;
			}
			ResourceServerAndSecret resourceServerAndSecret = new ResourceServerAndSecret();
			resourceServerAndSecret.setResourceServer(resourceServer);
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
		Map<String, Map<String, ResourceServer>> resourceServersMultiMap = resourceServerManager.getResourceServersMultiMap();
		
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
