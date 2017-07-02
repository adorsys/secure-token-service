package de.adorsys.sts.user;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds User resources. For example:
 * 
 * @author fpo
 *
 */
public class UserCredentials {
	
	// Map of credentials per resource server.
	private Map<String, String> credentialForResourceServerMap = new HashMap<>();

	public String getCredentialForResourceServer(String resourceServer) {
		return credentialForResourceServerMap.get(resourceServer);
	}
	
	public void setCredentialForResourceServer(String resourceServer, String credential) {
		credentialForResourceServerMap.put(resourceServer, credential);
		if(credentialForResourceServerMap.size()>50) throw new IllegalStateException("Supports up to 50 resource servers");
	}

	public void deleteCredentialForResourceServer(String resourceServer, String credential) {
		credentialForResourceServerMap.remove(resourceServer);
	}

	public Map<String, String> getCredentialForResourceServerMap() {
		return credentialForResourceServerMap;
	}

	public void setCredentialForResourceServerMap(Map<String, String> credentialForResourceServerMap) {
		this.credentialForResourceServerMap = credentialForResourceServerMap;
	}
	
	
}
