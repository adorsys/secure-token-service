package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.JpaPersistenceAutoConfiguration;
import de.adorsys.sts.tests.config.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@JpaPersistenceAutoConfiguration
@ContextConfiguration(classes = {
        WithServerInfo.class,
        WithAdminConfig.class,
        WithPopConfig.class,
        WithTokenExchangeConfig.class,
        WithoutWebSecurityConfig.class
})
class ServerInfoControllerJpaTest extends BaseEndpointTest {

    @Test
    @SneakyThrows
    void serverInfoTest() {
        mvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwks_url").value("http://localhost/pop"))
                .andExpect(jsonPath("$.token_exchange").value("http://localhost/token"))
                .andExpect(jsonPath("$.admin_url").value("http://localhost/admin"))
                .andExpect(jsonPath("$.api_docs_url").value("http://localhost/v3/api-docs"));
    }
}
