package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {
        WithTokenExchangeConfig.class,
        WithControllableClock.class,
        WithoutWebSecurityConfig.class})
class TokenExchangeControllerJpaTest extends BaseEndpointTest {

    @Autowired
    private WithControllableClock.ClockTestable clock;

    private static final String token =
            "eyJraWQiOiJhYmMiLCJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3lvdXItaWRwLWhvc3RuYW1lL2F1d" +
                    "GgvcmVhbG1zL3lvdXItcmVhbG0iLCJodHRwOi8vZXhhbXBsZS5jb20vaXNfcm9vdCI6dHJ1ZSwic3ViIjoiZGV2IiwibmFtZSI6ImU" +
                    "yZSB0ZXN0IiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE2MDA4MTkzODB9._S742nPRhcog8sDXPgy94pwVYcqgHiGEPn-jn2YEQbY";

    @Test
    @SneakyThrows
    void tokenExchangeTest() {

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
                .andExpect(content().string("{" +
                        "\"access_token\":\"eyJraWQiOiJzdHMtc2VjcmV0LXNlcnZlci1kZXYtZmYwMGZkNzgtMWQyNC00NDQxLWIzYjYtZGYxYWUxY2FlOTRiIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ." +
                        "eyJzdWIiOiJkZXYiLCJyb2xlIjoiVVNFUiIsImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdCIsInR5cCI6IkJlYXJlciIsInNlY3JldCI6eyJzdHMiOiJleUpyYVdRaU9pSnpkSE10WlhoaGJYQ" +
                        "nNaUzFQVmxOR1FpSXNJbVZ1WXlJNklrRXhNamhIUTAwaUxDSmhiR2NpT2lKU1UwRXRUMEZGVUNKOS5UMy1PX0VKX3JiVldjck85bmpjMElwZXNNc1VDVHdCQkcyVy1hOC1hYXU2cmV5WEpsRl" +
                        "FQenZEa2czMlN3ZkhLb08zeW1LNWNOcUk5WkQ3RXJjWmxaS1NId0J1QzVRQlRZU3RSREc2NTUyOUtoQXhhbHJIM01FQTFwSW5HVDNGeWJVLVVLM0VWTVlZY21XLTlfWXZ4bXc5VjVLTVZiSlo" +
                        "3WDV2TjhCRm4wbUMwNFNvYkRNdTBtdkdQNGYzZ3RvZUh1cXlDTTZzdURzSDFlMVdCa3FZQm9iQ2xQdlcySWFjWkt1TFVjVFM3bFdaOTlJajJycjdDR2tVR2ExdDBrT19rOVlQRkY5MkMyQXlR" +
                        "RmhTdGswMUIwQXNQNXI2S3hJdGR0d3FyS3VtSEFBY0tvakpaSUtQVlRITUE5cGNNNXJUZHdPM3czbm5ta0RLRnFUT1dLV1B5bWcuOEpyMlZWM1VGY3RpX1RGei5TSjVyR205a2lFWTl6ZjlNM" +
                        "XdfVnNqOFp4VlZ5S2RISUFrUkg4S2E2YzRlMllpTnB4cmRFV2tJdnlRLllLTzlidlRVTVdMZ0tuTHhsUHRzOXcifSwiZXhwIjoxNjAwODE5MzgwLCJpYXQiOjE1MTYyMzkwMjIsImp0aSI6Ij" +
                        "c1NDk3ZTU1LTY1NTgtNDk2MS05OTQ0LTllYjdkZmJhNTJiMiJ9.8Urq0or-A_FI40lSZwuzWXgpreUNxdM1yBUUHHs1lYGX5wyWfWzrQuUtzQzWzXfLDblT65vxMvVB-u7tn-O8aZEMUuV3kT" +
                        "Vx5V_6BZ3qYzZ-CcVq79JK9jpPyKzec5Ez2wOcFoXv_0TYzRVRLpF13yWWikS0Pr8v8UJ6ASCDcNtRfPE8PgjsaidSBCI9Mm-Zkh3q6L5qH7-Bh3ktsB6YF_C_C4u24P5tfnjCl03nScLQNoT" +
                        "tCRMRx0byZODudSGelCMtOWqubmBRPQklm7ymTJc9IkTOSXnGXTWkSURNb3-Tnrfu7nlyb9IVFP1wVa8Cr36kxfDGt5rbXwspDXd4Og\"," +
                        "\"issued_token_type\":\"urn:ietf:params:oauth:token-type:access_token\"," +
                        "\"token_type\":\"Bearer\"," +
                        "\"expires_in\":84580358," +
                        "\"scope\":\"\"," +
                        "\"refresh_token\":null}\n"))
                .andReturn();
    }
}
