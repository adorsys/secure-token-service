package de.adorsys.sts.keycloak.rest;

import org.apache.http.HttpStatus;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.ServicesLogger;

import java.io.IOException;
import java.util.List;

public class CustomRestClient {

    public static String loadUserSecrets(
            KeycloakSession session,
            String url,
            String username,
            String password,
            List<String> audiences
    ) throws IOException {

        CustomLoginRequest loginRequest = CustomLoginRequest.builder()
                .username(username)
                .password(password)
                .audiences(audiences)
                .build();

        SimpleHttp.Response res = SimpleHttp.doPost(url, session).json(loginRequest).asResponse();

        int status = res.getStatus();
        if (status == HttpStatus.SC_OK) {
            return res.asString();
        } else {
            ServicesLogger.LOGGER.debug("Cannot load secrets for user: " + username + "; status: " + status + "; reason: " + res.asString());
        }

        return null;
    }
}
