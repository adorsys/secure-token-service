package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;

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
	public void parseRoles(JWTClaimsSet claimSet, final List<String> result){		
		// Realm roles
		JSONObject objectClaim = readClaim(claimSet, "realm_access"); 
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
	
	private JSONObject readClaim(JWTClaimsSet claimSet, String claimName){
		try {
			return claimSet.getJSONObjectClaim(claimName);
		} catch (ParseException e) {
			// TODO log exception.
			return null;
		}
	}
}
