package de.adorsys.sts.token;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description="Carries the response of a token request", value="TokenResponse" )
public class TokenResponse  {
	public static final String TOKEN_EXCHANGE_GRANT_TYPE="urn:ietf:params:oauth:grant-type:token-exchange";
	public static final String ISSUED_TOKEN_TYPE_ACCESS_TOKEN="urn:ietf:params:oauth:grant-type:token-exchange";
	public static final String TOKEN_TYPE_BEARER = "Bearer";

	@ApiModelProperty(value = "The security token issued by the authorization server in response to the token exchange request.", required=true, example="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
	private String access_token;
	
	@ApiModelProperty(value = "An identifier for the representation of the issued security token. Can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token", 
			required=true, example="urn:ietf:params:oauth:token-type:access_token")
	private String issued_token_type;
	
	@ApiModelProperty(value = "It provides the client with information about how to utilize the access token to access protected resources.  For example, a value of Bearer", 
			required=true, example="Bearer")
	private String token_type;
	
	@ApiModelProperty(value = "The validity lifetime, in seconds, of the token issued by the authorization server. For example, the value 1800 denotes that the token will expire in thirty minutes from the time the response was generated.", 
				required=false, example="1800")
	public int expires_in;
	
	@ApiModelProperty(value = "OPTIONAL, if the scope of the issued security token is identical to the scope requested by the client; otherwise, REQUIRED.", 
			required=false, example="user banking")
	private String scope;

	@ApiModelProperty(value = "The security token issued by the authorization server in response to the token exchange request. In this case the issued_token_type wil be urn:ietf:params:oauth:token-type:refresh_token.", 
			required=true, example="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
	private String refresh_token;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getIssued_token_type() {
		return issued_token_type;
	}

	public void setIssued_token_type(String issued_token_type) {
		this.issued_token_type = issued_token_type;
	}

	public String getToken_type() {
		return token_type;
	}

	public void setToken_type(String token_type) {
		this.token_type = token_type;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}
	
	
}
