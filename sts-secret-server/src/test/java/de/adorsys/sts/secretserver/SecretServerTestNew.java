package de.adorsys.sts.x;

import com.nimbusds.jwt.JWTClaimsSet;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.secretserver.configuration.TestConfiguration;
import de.adorsys.sts.secretserver.helper.Authentication;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.client.RestTokenExchangeClient;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest(properties = "spring.main.banner-mode=off", webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("IT")
@DirtiesContext
@Testcontainers
@ContextConfiguration(
        classes = {TestConfiguration.class},
        initializers = {ConfigDataApplicationContextInitializer.class}
)
public class SecretServerTestNew {

    private static final String MOPED_CLIENT_AUDIENCE = "moped-client";

    private static final String USERNAME_ONE = "user1";
    private static final String PASSWORD_ONE = "user1_pwd";

    private static final String USERNAME_TWO = "user2";
    private static final String PASSWORD_TWO = "user2_pwd";

    @Autowired
    DecryptionService decryptionService;

    @Autowired
    Authentication authentication;

    @Autowired
    BearerTokenValidator bearerTokenValidator;

    @Autowired
    TestRestTemplate restTemplate;

    private RestTokenExchangeClient client;

    @Container
    public KeycloakContainer keycloak = new KeycloakContainer().withAdminUsername("admin")
            .withProviderClassesFrom("target/classes/")
            .withRealmImportFile("moped.json")
            .withAdminPassword("datevsb_12345");

    @Before
    public void setup() {
        RestTemplate restTemplate = this.restTemplate.getRestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        client = new RestTokenExchangeClient(restTemplate);
    }

    @Test
    void shouldReturnTheSameSecretForSameUser() {
        String firstSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);
        String secondSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);

        assertThat(firstSecret, is(equalTo(secondSecret)));
    }

    private String getDecryptedSecret(String username, String password) {
        TokenResponse secretServerToken = getSecretServerToken(username, password);
        return extractSecretFromToken(secretServerToken.getAccess_token());
    }

    private String extractSecretFromToken(String secretServerAccessToken) {
        Map<String, String> secrets = extractSecretsFromToken(secretServerAccessToken);
        return decryptionService.decrypt(secrets.get(MOPED_CLIENT_AUDIENCE));
    }

    private Map<String, String> extractSecretsFromToken(String secretServerAccessToken) {
        BearerToken exchangedToken = bearerTokenValidator.extract(secretServerAccessToken);
        JWTClaimsSet claims = exchangedToken.getClaims();

        Object secretClaimAsObject = claims.getClaim("secret");

        return (Map) secretClaimAsObject;
    }

    private TokenResponse getSecretServerToken(String username, String password) {
        Authentication.AuthenticationToken authToken = authentication.login(username, password);
        return getTokenForAccessToken(authToken.getAccessToken());
    }

    private TokenResponse getTokenForAccessToken(String accessToken) {
        return client.exchangeToken("/secret-server/token-exchange", MOPED_CLIENT_AUDIENCE, accessToken);
    }
}
