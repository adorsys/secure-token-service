package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.Resource;
import de.adorsys.sts.tests.config.WithAdminConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {WithAdminConfig.class, WithoutWebSecurityConfig.class})
class AdminControllerJpaTest extends BaseEndpointTest {

    @Test
    @SneakyThrows
    void getResourseServersTest() {
        String expectedContent = Resource.read("fixture/admin_controller_response.json");

        mvc.perform(get("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void postResourceServerTest() {
        String requestJson = Resource.read(("fixture/admin_controller_request.json"));

        mvc.perform(post("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJson))
                .andExpect(status().is(204))
        .andReturn();

    }
}
