package de.adorsys.sts.keycloak.rest;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.keycloak.services.ServicesLogger;

import java.util.List;

public class CustomRestClient {

    public static String loadUserSecrets(
            String url,
            String username,
            String password,
            List<String> audiences
    ) throws CustomAuthenticationException {
        CustomLoginRequest loginRequest = CustomLoginRequest.builder()
                .username(username)
                .password(password)
                .audiences(audiences)
                .build();

        Response res = ClientBuilder.newBuilder()
                .build()
                .target(url)
                .request()
                .post(Entity.json(loginRequest));

        int status = res.getStatus();
        if (status == HttpStatus.SC_OK) {
            return res.readEntity(String.class);
        } else {
            ServicesLogger.LOGGER.debug("Cannot load secrets for user: " + username + "; status: " + status + "; reason: " + res.getStatusInfo().getReasonPhrase());
        }

        return null;
    }
}
