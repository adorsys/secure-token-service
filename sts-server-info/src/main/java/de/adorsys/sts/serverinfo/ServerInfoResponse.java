package de.adorsys.sts.serverinfo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Displays Endpoint Metadata")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerInfoResponse {

    @Schema(description = "Returns the endpoint url for the JSON Web Keys Set as specified in https://tools.ietf.org/html/rfc7517")
    private String jwks_url;
    @Schema(description = "Returns the token exchange endpoint as specified in https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08")
    private String token_exchange;
    @Schema(description = "Returns the admin endpoint")
    private String admin_url;
    @Schema(description = "The api docs url")
    private String api_docs_url;

    public String getJwks_url() {
        return jwks_url;
    }

    public void setJwks_url(String jwks_url) {
        this.jwks_url = jwks_url;
    }

    public String getToken_exchange() {
        return token_exchange;
    }

    public void setToken_exchange(String token_exchange) {
        this.token_exchange = token_exchange;
    }

    public String getAdmin_url() {
        return admin_url;
    }

    public void setAdmin_url(String admin_url) {
        this.admin_url = admin_url;
    }

    public String getApi_docs_url() {
        return api_docs_url;
    }

    public void setApi_docs_url(String api_docs_url) {
        this.api_docs_url = api_docs_url;
    }

}
