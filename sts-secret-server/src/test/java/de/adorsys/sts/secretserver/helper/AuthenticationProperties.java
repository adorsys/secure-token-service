package de.adorsys.sts.secretserver.helper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "tests.authentication")
@Validated
public class AuthenticationProperties {

    @NotNull
    private String clientId;

    @NotNull
    private String clientSecret;

    @NotNull
    private String accessTokenUri;

    @NotNull
    private String userInfoUri;

    @NotNull
    private String endSessionEndpoint;

    private Boolean useSecureCookies = true;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessTokenUri() {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri) {
        this.accessTokenUri = accessTokenUri;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }

    public void setUserInfoUri(String userInfoUri) {
        this.userInfoUri = userInfoUri;
    }

    public String getEndSessionEndpoint() {
        return endSessionEndpoint;
    }

    public void setEndSessionEndpoint(String endSessionEndpoint) {
        this.endSessionEndpoint = endSessionEndpoint;
    }

    public Boolean getUseSecureCookies() {
        return useSecureCookies;
    }

    public void setUseSecureCookies(Boolean useSecureCookies) {
        this.useSecureCookies = useSecureCookies;
    }
}