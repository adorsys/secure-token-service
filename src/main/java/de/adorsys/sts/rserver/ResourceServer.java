package de.adorsys.sts.rserver;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Describes a resource server", value="ResourceServer" )
public class ResourceServer {
	
	@ApiModelProperty(value = "The resource server's endpoint url", 
			required=true, example="http://localhost:8080/multibanking-service")
	private String endpointUrl;
	
	@ApiModelProperty(value = "The user secret claim name. Value will be used to encrypt and decrypt proctected resources in the resource server's realm", 
			required=false, example="userSecret")
	private String userSecretClaimName;
	
	@ApiModelProperty(value = "States that this is an idp server. This exchange server will be accespt tokens produces by the resource server.", 
			required=false, example="true", allowableValues="true,false")	
	private boolean idpServer;

	@ApiModelProperty(value = "The json web key set url", 
			required=false, example="http://localhost:8080/multibanking-service/pop")	
	private String jwksUrl;
	
	@ApiModelProperty(value = "The client id of this server in the realm of the exchange server", 
			required=false, example="multibanking-service")	
	private String clientId;

	@ApiModelProperty(value = "The audience of this server in the realm of the exchange server", 
			required=false, example="multibanking-service")	
	private String audience;

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public void setEndpointUrl(String endpointUrl) {
		this.endpointUrl = endpointUrl;
	}

	public String getUserSecretClaimName() {
		return userSecretClaimName;
	}

	public void setUserSecretClaimName(String userSecretClaimName) {
		this.userSecretClaimName = userSecretClaimName;
	}

	public boolean isIdpServer() {
		return idpServer;
	}

	public void setIdpServer(boolean idpServer) {
		this.idpServer = idpServer;
	}

	public String getJwksUrl() {
		return jwksUrl;
	}

	public void setJwksUrl(String jwksUrl) {
		this.jwksUrl = jwksUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAudience() {
		return audience;
	}

	public void setAudience(String audience) {
		this.audience = audience;
	}
}
