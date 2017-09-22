package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;

import java.text.ParseException;
import java.util.List;

public class StringListRolesParser {

	public void extractRoles(JWTClaimsSet claimSet, String claimName, final List<String> results) {
		List<String> scpClaims = readClaim(claimSet, claimName);
		if(scpClaims!=null && !scpClaims.isEmpty()){
			for (String scp : scpClaims) {
				if(scp!=null && !results.contains(scp)){
					results.add(scp);
				}
			}
		}
	}
	
	private List<String> readClaim(JWTClaimsSet claimSet, String claimName){
		try {
			return claimSet.getStringListClaim(claimName);
		} catch (ParseException e) {
			// TODO log exception.
			return null;
		}
	}
	
}
