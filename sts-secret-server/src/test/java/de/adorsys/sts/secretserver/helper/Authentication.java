package de.adorsys.sts.secretserver.helper;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class Authentication {
    private static final Logger logger = LoggerFactory.getLogger(Authentication.class);

    private static final HttpHeaders EMPTY_HEADERS = new HttpHeaders();
    private static final MultiValueMap<String, String> EMPTY_BODY = new LinkedMultiValueMap<>();

    private static final String REFRESH_TOKEN_PARAM_NAME = "refresh_token";
    private static final String ACCESS_TOKEN_PARAM_NAME = "access_token";
    private static final String CLIENT_SECRET_PARAM_NAME = "client_secret";
    private static final String CLIENT_ID_PARAM_NAME = "client_id";
    private static final String GRANT_TYPE_PARAM_NAME = "grant_type";
    private static final String REFRESH_TOKEN_GRANT_TYPE = "refresh_token";
    private static final String USERNAME_PARAM_NAME = "username";
    private static final String PASSWORD_PARAM_NAME = "password";
    private static final String GRANT_TYPE_PASSWORD = "password";

    private final RestTemplate restTemplate;
    private final AuthenticationProperties authenticationProperties;

    @Autowired
    public Authentication(RestTemplate restTemplate, AuthenticationProperties authenticationProperties) {
        this.restTemplate = restTemplate;
        this.authenticationProperties = authenticationProperties;
    }

    public AuthenticationToken login(String username, String password) {
        Optional<AuthenticationToken> maybeToken = tryToLogin(username, password);
        return maybeToken.orElseThrow(RuntimeException::new);
    }

    public Optional<AuthenticationToken> tryToLogin(String username, String password) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add(CLIENT_ID_PARAM_NAME, authenticationProperties.getClientId());
        body.add(CLIENT_SECRET_PARAM_NAME, authenticationProperties.getClientSecret());
        body.add(GRANT_TYPE_PARAM_NAME, GRANT_TYPE_PASSWORD);
        body.add(USERNAME_PARAM_NAME, username);
        body.add(PASSWORD_PARAM_NAME, password);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, EMPTY_HEADERS);

        return requestAuthenticationToken(request);
    }

    private Optional<AuthenticationToken> requestAuthenticationToken(HttpEntity<MultiValueMap<String, String>> request) {
        Optional<AuthenticationToken> token;

        try {
            ResponseEntity<AuthenticationToken> response = restTemplate.exchange(
                    authenticationProperties.getAccessTokenUri(),
                    HttpMethod.POST,
                    request,
                    AuthenticationToken.class
            );
            token = Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "Request {} on {} not successful: {} {} {} {}",
                        HttpMethod.POST,
                        authenticationProperties.getAccessTokenUri(),
                        e.getRawStatusCode(),
                        e.getStatusCode(),
                        e.getStatusText(),
                        e.getResponseBodyAsString()
                );
            }

            token = Optional.empty();
        }

        return token;
    }

    public static class AuthenticationToken {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("refresh_token")
        private String refreshToken;

        @JsonProperty("expires_in")
        private Long expiresIn;

        @JsonProperty("refresh_expires_in")
        private Long refreshExpiresIn;

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }

        public Long getRefreshExpiresIn() {
            return refreshExpiresIn;
        }
    }
}
