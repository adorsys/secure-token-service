package de.adorsys.sts.tests.e2e.tokenexchange;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.Resource;
import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.config.WithTokenExchangeConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import de.adorsys.sts.token.tokenexchange.server.TokenExchangeRestController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {
        WithTokenExchangeConfig.class,
        WithControllableClock.class,
        WithoutWebSecurityConfig.class})
class TokenExchangeControllerJpaTest extends BaseEndpointTest {

    @Autowired
    private WithControllableClock.ClockTestable clock;

    @Test
    @SneakyThrows
    void tokenExchangeTest() {
        String token = Resource.read("fixture/test_token.txt");

        clock.setInstant(Instant.ofEpochMilli(1516239022000L));

        mvc.perform(post(TokenExchangeRestController.DEFAULT_PATH)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        .param("grant_type", TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE)
                        .param("resource", "1, 2, 3")
                        .param("audience", "sts")
                        .param("scope", "")
                        .param("requested_token_type", "")
                        .param("subject_token", token)
                        .param("subject_token_type", TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
                        .param("actor_token", "")
                        .param("actor_token_type", "")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issued_token_type").value("urn:ietf:params:oauth:token-type:access_token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value("84580358"));
    }
}
