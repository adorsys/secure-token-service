package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.WithPasswordGrantConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
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
                .andExpect(content().string("{\"" +
                        "access_token\":\"eyJraWQiOiJzdHMtc2VjcmV0LXNlcnZlci1kZXYtOThmNzAzZWEtM2EwZS00MGMwLWIyYWUtNmIzMmI4N2I1NzZhIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJtYXgubXVzdGVybWFuIiwibmJmIjoxNTY4MTg1NTgyLCJyb2xlIjoiVVNFUiIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdCIsInR5cCI6IkJlYXJlciIsImV4cCI6MTU2ODE4NTg4MiwiaWF0IjoxNTY4MTg1NTgyLCJqdGkiOiI5NDU1YjJiOS0zOGJmLTRkMGEtYmIxNS00YTA0ZmE0ZDRjNTEifQ.VVcGBWMZRDZvKbemyXMpUx8JNbfN1TkQh7kMElfgv1TsaOlpZeN5hdmBvSrgP2lFmbw9jgzv9xuHvX9z_ySA5VU2XXGEMPjYTz4DtsJkgsoguRpxyf8K9TPUsSFhW_cSgQwnPiab2Fpsz6a1LVIZHBjpGHYuQigAjWoSE7FVIUsv9-scdTqDa6JK9d0SQlKYcNu66b4uBWj63B0HG7Kx8EhBUb7DxXTdrfZC-Qa3SGZjg4Esyw_W_QIOMxO5Oq_MP-C_94sRB3kcQRQSBwTVDQCYBNu1oGJmeOuKnl8R-IIfnJ48Y-vjfxhQbgATJU9BA1pmBshfD3oTl-3F4bwhew\"," +
                        "\"issued_token_type\":\"urn:ietf:params:oauth:token-type:access_token\"," +
                        "\"token_type\":\"Bearer\"," +
                        "\"expires_in\":299," +
                        "\"scope\":null," +
                        "\"refresh_token\":null}\n"))
                .andDo(print());
    }
}
