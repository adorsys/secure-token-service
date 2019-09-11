package de.adorsys.sts.tests.e2e;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.WithAdminConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import java.net.URL;

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
        String expectedContent = readFromResource("fixture/admin_controller_response.json");

        mvc.perform(get("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedContent))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void postResourceServerTest() {
        String requestJson = readFromResource("fixture/admin_controller_request.json");

        mvc.perform(post("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJson))
                .andExpect(status().is(204))
        .andReturn();

    }

    @SneakyThrows
    private String readFromResource(String path) {
        URL url = Resources.getResource(path);
        return Resources.toString(url, Charsets.UTF_8);
    }
}
