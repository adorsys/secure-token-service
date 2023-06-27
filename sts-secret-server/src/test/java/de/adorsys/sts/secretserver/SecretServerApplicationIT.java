//package de.adorsys.sts.secretserver;
//
//import com.nimbusds.jwt.JWTClaimsSet;
//import de.adorsys.sts.keymanagement.service.DecryptionService;
//import de.adorsys.sts.secretserver.configuration.TestConfiguration;
//import de.adorsys.sts.secretserver.helper.Authentication;
//import de.adorsys.sts.token.api.TokenResponse;
//import de.adorsys.sts.token.tokenexchange.client.RestTokenExchangeClient;
//import de.adorsys.sts.tokenauth.BearerToken;
//import de.adorsys.sts.tokenauth.BearerTokenValidator;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.web.client.DefaultResponseErrorHandler;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Map;
//
//import static com.googlecode.catchexception.CatchException.catchException;
//import static com.googlecode.catchexception.CatchException.caughtException;
//import static org.hamcrest.CoreMatchers.not;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.Is.is;
//import static org.hamcrest.core.IsEqual.equalTo;
//import static org.hamcrest.core.IsInstanceOf.instanceOf;
//import static org.hamcrest.core.IsNull.notNullValue;
//
//@ContextConfiguration(
//        classes = {TestConfiguration.class},
//        initializers = {ConfigDataApplicationContextInitializer.class}
//)
//@SpringBootTest(properties="spring.main.banner-mode=off", webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@ActiveProfiles("IT")
//@DirtiesContext
//public class SecretServerApplicationIT {
//
//
//    @Test
//    public void shouldReturnDifferentSecretsForDifferentUsers() throws Exception {
//        String firstSecret = getDecryptedSecret(USERNAME_ONE, PASSWORD_ONE);
//        String secondSecret = getDecryptedSecret(USERNAME_TWO, PASSWORD_TWO);
//
//        assertThat(firstSecret, is(not(equalTo(secondSecret))));
//    }
//
//    @Test
//    public void shouldNotReturnTheSameTokenForSameUser() throws Exception {
//        TokenResponse firstTokenResponse = getSecretServerToken(USERNAME_ONE, PASSWORD_ONE);
//        assertThat(firstTokenResponse.getAccess_token(), is(notNullValue()));
//
//        TokenResponse secondTokenResponse = getSecretServerToken(USERNAME_ONE, PASSWORD_ONE);
//        assertThat(secondTokenResponse.getAccess_token(), is(notNullValue()));
//
//        assertThat(firstTokenResponse, is(not(equalTo(secondTokenResponse))));
//    }
//
//    @Test
//    public void shouldNotGetSecretForInvalidAccessToken() throws Exception {
//        final String invalidAccessToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJvVjU2Uk9namthbTVzUmVqdjF6b1JVNmY" +
//                "1R3YtUGRTdjN2b1ZfRVY5MmxnIn0.eyJqdGkiOiI5NWY2MzQ4NC04MTk2LTQ2NzYtYjI4Ni1lYjY4YTFmOTZmYTAiLCJleHAiOjE1N" +
//                "TUwNDg5MzIsIm5iZiI6MCwiaWF0IjoxNTU1MDQ4NjMyLCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjMyODU0L2F1dGgvcmVhbG1zL21" +
//                "vcGVkIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImJiNjNkN2Y2LWFhZjUtNDc5My1iNjA0LTY2NWZhMzU0YmU0MSIsInR5cCI6IkJlY" +
//                "XJlciIsImF6cCI6Im1vcGVkLWNsaWVudCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImZiZTU3ODNlLTE5NmUtNGM5Yi0" +
//                "4OThhLTVkMmE2MDQ1MmM0NSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb" +
//                "3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2N" +
//                "vcGUiOiJwcm9maWxlIGVtYWlsIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoiTXkgVXNlciAxIiwicHJlZmVycmVkX3VzZ" +
//                "XJuYW1lIjoidXNlcjEiLCJnaXZlbl9uYW1lIjoiTXkiLCJmYW1pbHlfbmFtZSI6IlVzZXIgMSIsImVtYWlsIjoibXkxQG1haWwuZGU" +
//                "ifQ.VMIYfwGNDc3j2JAp_ZIXaITpwTnamYEMBX_FxVuS55_t3bbxx4WjR7N2zBwUlVd6HaxrHBPvbCyUzEhhjtP5BJcHaS1kN4A3zv" +
//                "215F_Za1gM-Im7wUQ9Ggg9bIPbWbHmjVBldk8oCGyeGIkGT5U12iJ376wFSX-IVHnfpAjgbRtfLKqYKS7zn0L0p2KZtjjdwz0CzG7r" +
//                "20qD2QfgDoA0CpOZCQzMe9WoIfo8L-g4099--XouFyMWRU8VyVsx_73ekNKPUmWvuNIxeF3PBk9KGs7ABUnv_6n8A-KqzYTyA4y0gU" +
//                "8E9mgIuWpDmQ2FROf1Gd-2it9k3tvr83k7N1dMvg";
//
//        catchException(client).exchangeToken("/secret-server/token-exchange", MOPED_CLIENT_AUDIENCE, invalidAccessToken);
//
//        Exception caughtException = caughtException();
//
//        assertThat(caughtException, instanceOf(HttpClientErrorException.class));
//        assertThat(((HttpClientErrorException) caughtException).getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
//    }
//
//    @Test
//    public void shouldNotGetSecretForFakeAccessToken() throws Exception {
//        final String fakeAccessToken = "my fake access token";
//
//        catchException(client).exchangeToken("/secret-server/token-exchange", MOPED_CLIENT_AUDIENCE, fakeAccessToken);
//
//        Exception caughtException = caughtException();
//
//        assertThat(caughtException, instanceOf(HttpClientErrorException.class));
//        assertThat(((HttpClientErrorException) caughtException).getStatusCode(), is(equalTo(HttpStatus.FORBIDDEN)));
//    }
//
//    @Test
//    public void shouldGetEmptySecretsForUnknownAudience() throws Exception {
//        Authentication.AuthenticationToken authToken = authentication.login(USERNAME_ONE, PASSWORD_ONE);
//
//        TokenResponse secretServerToken = client.exchangeToken("/secret-server/token-exchange", "unknown audience", authToken.getAccessToken());
//
//        Map<String, String> secrets = extractSecretsFromToken(secretServerToken.getAccess_token());
//        assertThat(secrets.size(), is(equalTo(0)));
//    }
//
//
//
//}
