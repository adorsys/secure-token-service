package de.adorsys.sts.token.tokenexchange.client;

import de.adorsys.sts.common.util.ImmutableLists;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class RestTokenExchangeClient implements TokenExchangeClient {

    private final RestTemplate restTemplate;

    public RestTokenExchangeClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TokenResponse exchangeToken(String uri, List<String> audiences, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        headers.add("Accept", "*/*");
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.addAll("audience", audiences);
        body.add("grant_type", TokenExchangeConstants.TOKEN_EXCHANGE_OAUTH_GRANT_TYPE);
        body.add("subject_token", accessToken);
        body.add("subject_token_type", TokenExchangeConstants.JWT_OAUTH_TOKEN_TYPE);

        ResponseEntity<TokenResponse> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                TokenResponse.class
        );

        return response.getBody();
    }

    public TokenResponse exchangeToken(String uri, String audience, String accessToken) {
        return exchangeToken(uri, ImmutableLists.of(audience), accessToken);
    }
}
