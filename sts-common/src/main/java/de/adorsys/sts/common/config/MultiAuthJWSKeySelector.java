package de.adorsys.sts.common.config;

import java.security.Key;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;

public class MultiAuthJWSKeySelector<C extends SecurityContext> implements JWSKeySelector<C> {

	private AuthServer authServer;
	
	@Override
	public List<? extends Key> selectJWSKeys(JWSHeader header, C context) throws KeySourceException {
		// Check signature
		String keyID = header.getKeyID();
		if(StringUtils.isBlank(keyID)) return Collections.emptyList();
		
		Key key = authServer.getJWK(keyID);
		if(key==null)return Collections.emptyList();
		
		return Collections.singletonList(key);
	}

	public MultiAuthJWSKeySelector(AuthServer authServer) {
		super();
		this.authServer = authServer;
	}
}
