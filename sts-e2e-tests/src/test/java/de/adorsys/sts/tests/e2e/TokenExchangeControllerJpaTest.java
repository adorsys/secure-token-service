package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.WithTokenExchangeConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import de.adorsys.sts.token.tokenexchange.server.TokenExchangeRestController;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {WithTokenExchangeConfig.class, WithoutWebSecurityConfig.class})
class TokenExchangeControllerJpaTest extends BaseEndpointTest {

    @Test
    @SneakyThrows
    void tokenExchangeTest() {

        mvc.perform(post(TokenExchangeRestController.DEFAULT_PATH)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .param("grant_type", TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE)
                        .param("resource", "1, 2, 3")
                        .param("audiences", "")
                        .param("scope", "")
                        .param("requested_token_type", "")
            .param("subject_token", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJNYXhNdXN0ZXJtYW4iLCJyb2xlIjoiVVNFUiIsImV4cCI6MTQ5NTM5MTAxM30.mN9eFMnEuYgh_KCULI8Gpm1X49wWaA67Ps1M7EFV0BQ")
            .param("subject_token_type", TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE)
                        .param("actor_token", "")
                        .param("actor_token_type", "")
        )
        .andDo(print())
            .andExpect(status().isOk())
            .andReturn();
    }
}
