package de.adorsys.sts.resourceserver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jose.util.Resource;
import com.nimbusds.jose.util.ResourceRetriever;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

public class ResourceServerInfo {
	public static final String jwks_url_key = "jwks_url";
	public static final String pop_key = "pop_url";
	
	private RemoteJWKSet<SecurityContext> jwkSource;
	
	private final ResourceRetriever resourceRetriever;

	private ResourceServer resourceServer;
	
	public ResourceServerInfo(final ResourceRetriever resourceRetriever, final ResourceServer resourceServer) {
		if (resourceServer.getEndpointUrl() == null && resourceServer.getJwksUrl()==null) {
			throw new IllegalArgumentException("Either meta data URL or jwks_url must not be null");
		}
		if (resourceRetriever == null) {
			throw new IllegalArgumentException("The resourceRetriever must not be null");
		}
		
		this.resourceRetriever = resourceRetriever;
		this.resourceServer = resourceServer;
		if(resourceServer.getJwksUrl()!=null){
			try {
				jwkSource = new RemoteJWKSet<>(new URL(resourceServer.getJwksUrl()), resourceRetriever);
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	
	public RemoteJWKSet<SecurityContext> getJWKSource() {
		
		if(jwkSource!=null) return jwkSource;

		Resource res;
		try {
			res = resourceRetriever.retrieveResource(new URL(resourceServer.getEndpointUrl()));
		} catch (IOException e) {
			throw new IllegalStateException("Couldn't retrieve remote metadata: " + e.getMessage(), e);
		}
		JSONObject jsonObject;
		try {
			jsonObject = JSONObjectUtils.parse(res.getContent());
		} catch (ParseException e) {
			throw new IllegalStateException("Couldn't parse remote metadata: " + e.getMessage(), e);
		}

		jwkSource = makeJwkSource(jsonObject, jwks_url_key);
		if(jwkSource!=null) return jwkSource;
		jwkSource = makeJwkSource(jsonObject, pop_key);
		if(jwkSource!=null) return jwkSource;
		
		// we can test if the object is already a jwks, in which case it will be used.
		try {
			JWKSet.parse(res.getContent());
			jwkSource = new RemoteJWKSet<>(new URL(resourceServer.getEndpointUrl()), resourceRetriever);
			return jwkSource;
		} catch (java.text.ParseException e) {
			// ignore.
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
		
		throw new IllegalStateException("No jwks url or pop_url provided for this server");
		
	}
	
	private RemoteJWKSet<SecurityContext> makeJwkSource(JSONObject jsonObject, String key){
		Object jwks_url = jsonObject.get(key);
		if(jwks_url!=null){
			try {
				return new RemoteJWKSet<>(new URL(jwks_url.toString()), resourceRetriever);
			} catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		}
		return null;
	}
}
