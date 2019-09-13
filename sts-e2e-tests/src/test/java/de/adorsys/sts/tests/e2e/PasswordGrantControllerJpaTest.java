package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.JpaPersistenceAutoConfiguration;
import de.adorsys.sts.tests.config.WithPasswordGrantConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@JpaPersistenceAutoConfiguration
@ContextConfiguration(classes = {WithPasswordGrantConfig.class, WithoutWebSecurityConfig.class})
class PasswordGrantControllerJpaTest extends BaseEndpointTest {

    @Test
    @SneakyThrows
    void testPasswordGrant() {
        mvc.perform(get("/token/password-grant")
                .param("grant_type", "password")
                .param("resource", "http://localhost:8080/multibanking-service")
                .param("audience", "http://localhost:8080/multibanking-service")
                .param("scope", "user banking")
                .param("username", "max.musterman")
                .param("password", "SamplePassword")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.issued_token_type").value("urn:ietf:params:oauth:token-type:access_token"))
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value("299"))
                .andExpect(jsonPath("$.issued_token_type").value("urn:ietf:params:oauth:token-type:access_token"))
                .andDo(print());
    }
}
