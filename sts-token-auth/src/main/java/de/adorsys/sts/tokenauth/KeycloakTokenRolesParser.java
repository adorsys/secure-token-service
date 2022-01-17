package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Parses keycloak roles and render them as scope strings.
 * 
 * @author fpo
 *
 */
public class KeycloakTokenRolesParser {
	private final Logger logger = LoggerFactory.getLogger(KeycloakTokenRolesParser.class);

	public void parseRoles(JWTClaimsSet claimSet, final List<String> result){		
		// Realm roles
		Map<String, Object> objectClaim = readClaim(claimSet, "realm_access");
		if(objectClaim!=null){
			Object roles = objectClaim.get("roles");
			addRoles(roles, result);
		}

		// Resource roles
		objectClaim = readClaim(claimSet, "resource_access"); 
		if(objectClaim!=null){
			Collection<Object> realms = objectClaim.values();
			for (Object reamlRoles : realms) {
				Map rolesMap = (Map)reamlRoles;
				Object roles = rolesMap.get("roles");
				addRoles(roles, result);
			}
		}
	}
	
	private void addRoles(Object roles, final List<String> result){
		if(roles==null) return;
		if(roles instanceof List){
			List list = (List) roles;
			for (Object item : list) {
				if(item!=null) {
					if(!result.contains(item)) result.add(item.toString());
				}
			}
		} else if (roles instanceof Object[]){
			Object[] list = (Object[]) roles;
			for (Object item : list) {
				if(item!=null) {
					if(!result.contains(item)) result.add(item.toString());
				}
			}
			
		}
	}
	
	private Map<String, Object> readClaim(JWTClaimsSet claimSet, String claimName){
		try {
			return claimSet.getJSONObjectClaim(claimName);
		} catch (ParseException e) {
			logger.warn("{} claim not found or not a JSON Object or Map", claimName);
			return null;
		}
	}
}
