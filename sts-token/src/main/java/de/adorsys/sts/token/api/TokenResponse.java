package de.adorsys.sts.token.api;


import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Carries the response of a token request")
public class TokenResponse {
    public static final String TOKEN_EXCHANGE_GRANT_TYPE = TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE;
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String ISSUED_TOKEN_TYPE_ACCESS_TOKEN = "urn:ietf:params:oauth:token-type:access_token";
    public static final String ISSUED_TOKEN_TYPE_REFRESH_TOKEN = "urn:ietf:params:oauth:grant-type:refresh_token";
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    @Schema(description = "The security token issued by the authorization server in response to the token exchange request.", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
    private String access_token;

    @Schema(description = "An identifier for the representation of the issued security token. Can be urn:ietf:params:oauth:token-type:access_token or urn:ietf:params:oauth:token-type:refresh_token",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "urn:ietf:params:oauth:token-type:access_token")
    private String issued_token_type;

    @Schema(description = "It provides the client with information about how to utilize the access token to access protected resources.  For example, a value of Bearer",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "Bearer")
    private String token_type;

    @Schema(description = "The validity lifetime, in seconds, of the token issued by the authorization server. For example, the value 1800 denotes that the token will expire in thirty minutes from the time the response was generated.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1800")
    public int expires_in;

    @Schema(description = "OPTIONAL, if the scope of the issued security token is identical to the scope requested by the client; otherwise, REQUIRED.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "user banking")
    private String scope;

    @Schema(description = "The security token issued by the authorization server in response to the token exchange request. In this case the issued_token_type wil be urn:ietf:params:oauth:token-type:refresh_token.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
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
