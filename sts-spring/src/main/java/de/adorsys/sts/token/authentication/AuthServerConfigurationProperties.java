package de.adorsys.sts.token.authentication;

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

    public static class AuthServerProperties {
        private String name;
        private String issUrl;
        private String jwksUrl;
        private Integer refreshIntervalSeconds = 600;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIssUrl() {
            return issUrl;
        }

        public void setIssUrl(String issUrl) {
            this.issUrl = issUrl;
        }

        public String getJwksUrl() {
            return jwksUrl;
        }

        public void setJwksUrl(String jwksUrl) {
            this.jwksUrl = jwksUrl;
        }

        public Integer getRefreshIntervalSeconds() {
            return refreshIntervalSeconds;
        }

        public void setRefreshIntervalSeconds(Integer refreshIntervalSeconds) {
            this.refreshIntervalSeconds = refreshIntervalSeconds;
        }
    }
}
