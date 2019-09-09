package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
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
                .andExpect(status().isOk())
                .andExpect(content().string("{" +
                        "\"jwks_url\":\"http://localhost/pop\"," +
                        "\"token_exchange\":\"http://localhost/token\"," +
                        "\"admin_url\":\"http://localhost/admin\"," +
                        "\"api_docs_url\":\"http://localhost/api-docs/index.html\"" +
                        "}"))
                .andReturn();
    }
}
