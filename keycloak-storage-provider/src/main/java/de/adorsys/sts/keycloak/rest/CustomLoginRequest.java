package de.adorsys.sts.keycloak.rest;

import java.util.ArrayList;
import java.util.List;


public class CustomLoginRequest {

    private String username;

    private String password;

    private List<String> audiences = new ArrayList<>();

    private CustomLoginRequest(String username, String password, List<String> audiences) {
        this.username = username;
        this.password = password;
        this.audiences = audiences;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<String> getAudiences() {
        return audiences;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String password;
        private List<String> audiences;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder audiences(List<String> audiences) {
            this.audiences = audiences;
            return this;
        }

        public CustomLoginRequest build() {
            return new CustomLoginRequest(username, password, audiences);
        }
    }
}
