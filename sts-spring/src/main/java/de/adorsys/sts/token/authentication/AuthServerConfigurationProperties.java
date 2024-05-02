package de.adorsys.sts.token.authentication;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "sts")
@Validated
public class AuthServerConfigurationProperties {

    private List<AuthServerProperties> authservers = new ArrayList<>();

    public List<AuthServerProperties> getAuthservers() {
        return authservers;
    }

    public void setAuthservers(List<AuthServerProperties> authservers) {
        this.authservers = authservers;
    }

    @Getter
    @Setter
    public static class AuthServerProperties {
        private String name;
        private String issUrl;
        private String jwksUrl;
        private Integer refreshIntervalSeconds = 600;
        private String keyCloakUrl;
    }
}
