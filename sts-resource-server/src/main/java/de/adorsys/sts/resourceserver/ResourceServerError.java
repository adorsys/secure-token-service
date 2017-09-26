package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.model.ResourceServer;

public class ResourceServerError {
	private String error;
	private ResourceServer resourceServer;
	
	
	public ResourceServerError() {
	}
	public ResourceServerError(String error, ResourceServer resourceServer) {
		super();
		this.error = error;
		this.resourceServer = resourceServer;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public ResourceServer getResourceServer() {
		return resourceServer;
	}
	public void setResourceServer(ResourceServer resourceServer) {
		this.resourceServer = resourceServer;
	}
}
