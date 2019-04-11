package de.adorsys.sts.secretserver;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.secretserver.configuration.TestConfiguration;
import de.adorsys.sts.secretserver.helper.Authentication;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.client.RestTokenExchangeClient;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {TestConfiguration.class},
        initializers = {ConfigFileApplicationContextInitializer.class}
)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("IT")
@DirtiesContext
public class SecretServerApplicationIT {
    private static final String MOPED_CLIENT_AUDIENCE = "moped-client";

    private static final String USERNAME_ONE = "user1";
    private static final String PASSWORD_ONE = "user1_pwd";

    private static final String USERNAME_TWO = "user2";
    private static final String PASSWORD_TWO = "user2_pwd";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    Authentication authentication;

    @Autowired
    BearerTokenValidator bearerTokenValidator;

    @Autowired
    DecryptionService decryptionService;

    private RestTokenExchangeClient client;

    @Before
    public void setup() throws Exception {
        RestTemplate restTemplate = this.restTemplate.getRestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

        client = new RestTokenExchangeClient(restTemplate);
    }

    @Test
    public void shouldReturnTheSameSecretForSameUser() throws Exception {
        String firstSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);
        String secondSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);

        assertThat(firstSecret, is(equalTo(secondSecret)));
    }

    @Test
    public void shouldReturnDifferentSecretsForDifferentUsers() throws Exception {
        String firstSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);
        String secondSecret = getDecryptedSecret(USERNAME_TWO, PASSWORD_TWO);

        assertThat(firstSecret, is(not(equalTo(secondSecret))));
    }

    @Test
    public void shouldNotReturnTheSameTokenForSameUser() throws Exception {
        TokenResponse firstTokenResponse = getSecretServerToken(USERNAME_ONE, PASSWORD_ONE);
        assertThat(firstTokenResponse.getAccess_token(), is(notNullValue()));

        TokenResponse secondTokenResponse = getSecretServerToken(USERNAME_ONE, PASSWORD_ONE);
        assertThat(secondTokenResponse.getAccess_token(), is(notNullValue()));

        assertThat(firstTokenResponse, is(not(equalTo(secondTokenResponse))));
    }

    private String getDecryptedSecret(String username, String password) {
        TokenResponse secretServerToken = getSecretServerToken(username, password);

        BearerToken exchangedToken = bearerTokenValidator.extract(secretServerToken.getAccess_token());
        JWTClaimsSet claims = exchangedToken.getClaims();

        Object secretClaimAsObject = claims.getClaim("secret");

        Map<String, String> secret = (Map) secretClaimAsObject;

        return decryptionService.decrypt(secret.get(MOPED_CLIENT_AUDIENCE));
    }

    private TokenResponse getSecretServerToken(String username, String password) {
        Authentication.AuthenticationToken authToken = authentication.login(username, password);
        return client.exchangeToken("/secret-server/token-exchange", MOPED_CLIENT_AUDIENCE, authToken.getAccessToken());
    }
}
