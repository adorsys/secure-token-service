package de.adorsys.sts.resourceserver.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Describes a resource server")
public class ResourceServer {

    @Schema(description = "The resource server's endpoint uri. This shall be the physical endpoint URL resource server.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "http://localhost:8080/multibanking-service")
    private String endpointUrl;

    @Schema(description = "The user secret claim name. Value will be used to encrypt and decrypt proctected resources in the resource server's realm",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "userSecret")
    private String userSecretClaimName;

    @Schema(description = "States that the referenced resource server is an idp server. This exchange server will be accespt tokens produces by the resource server.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "true", allowableValues = "true,false")
    private boolean idpServer;

    @Schema(description = "The physical json web key set url of the resource server",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "http://localhost:8080/multibanking-service/pop")
    private String jwksUrl;

    @Schema(description = "The client id of the referenced resource server in the realm of this token exchange server",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "multibanking-service")
    private String clientId;

    @Schema(description = "The audience of this server in the realm of the exchange server. This is the audience field in the access token. This can also be an URI, but must not match the pysical address of the resource server",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "multibanking-service")
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((audience == null) ? 0 : audience.hashCode());
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        result = prime * result + ((endpointUrl == null) ? 0 : endpointUrl.hashCode());
        result = prime * result + (idpServer ? 1231 : 1237);
        result = prime * result + ((jwksUrl == null) ? 0 : jwksUrl.hashCode());
        result = prime * result + ((userSecretClaimName == null) ? 0 : userSecretClaimName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ResourceServer other = (ResourceServer) obj;
        if (audience == null) {
            if (other.audience != null)
                return false;
        } else if (!audience.equals(other.audience))
            return false;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        if (endpointUrl == null) {
            if (other.endpointUrl != null)
                return false;
        } else if (!endpointUrl.equals(other.endpointUrl))
            return false;
        if (idpServer != other.idpServer)
            return false;
        if (jwksUrl == null) {
            if (other.jwksUrl != null)
                return false;
        } else if (!jwksUrl.equals(other.jwksUrl))
            return false;
        if (userSecretClaimName == null) {
            if (other.userSecretClaimName != null)
                return false;
        } else if (!userSecretClaimName.equals(other.userSecretClaimName))
            return false;
        return true;
    }


}
