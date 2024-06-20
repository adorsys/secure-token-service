package de.adorsys.sts.token.api;


import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;

public class TokenResponse {
    public static final String TOKEN_EXCHANGE_GRANT_TYPE = TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE;
    public static final String PASSWORD_GRANT_TYPE = "password";
    public static final String ISSUED_TOKEN_TYPE_ACCESS_TOKEN = "urn:ietf:params:oauth:token-type:access_token";
    public static final String ISSUED_TOKEN_TYPE_REFRESH_TOKEN = "urn:ietf:params:oauth:grant-type:refresh_token";
    public static final String TOKEN_TYPE_BEARER = "Bearer";

    private String access_token;

    private String issued_token_type;

    private String token_type;

    public int expires_in;

    private String scope;

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
