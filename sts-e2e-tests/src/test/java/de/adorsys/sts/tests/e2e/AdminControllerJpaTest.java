package de.adorsys.sts.tests.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.tests.BaseEndpointTest;
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
        mvc.perform(get("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "{\"servers\":[" +
                                "{\"endpointUrl\":null," +
                                "\"userSecretClaimName\":null," +
                                "\"idpServer\":false," +
                                "\"jwksUrl\":\"http://localhost:8888/pop\"," +
                                "\"clientId\":null," +
                                "\"audience\":\"sts\"" +
                                "},{" +
                                "\"endpointUrl\":null," +
                                "\"userSecretClaimName\":null," +
                                "\"idpServer\":false," +
                                "\"jwksUrl\":\"http://localhost:8887/pop\"," +
                                "\"clientId\":null," +
                                "\"audience\":\"sts-service-component-example\"" +
                                "},{" +
                                "\"endpointUrl\":null," +
                                "\"userSecretClaimName\":null," +
                                "\"idpServer\":false," +
                                "\"jwksUrl\":\"http://sts-service-component:8887/service-component/pop\"," +
                                "\"clientId\":null," +
                                "\"audience\":\"sts-service-component\"}" +
                                "]}"))
                .andReturn();
    }

    @Test
    @SneakyThrows
    void postResourceServerTest() {

        ResourceServer requestBody = new ResourceServer();
        requestBody.setAudience("add-example");
        requestBody.setJwksUrl("http://localhost:8000/test");

        requestBody.setEndpointUrl("http://localhost:8080/multibanking-service");
        requestBody.setUserSecretClaimName("userSecret");
        requestBody.setIdpServer(false);
        requestBody.setClientId("sts-tests");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(requestBody);

        mvc.perform(post("/admin/resourceServer/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJson))
                .andExpect(status().is(204))
        .andReturn();

    }
}
