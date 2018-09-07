package de.adorsys.sts.token.tokenexchange;

import de.adorsys.sts.token.api.TokenResponse;

import java.util.List;

public interface TokenExchangeClient {
    TokenResponse exchangeToken(String uri, List<String> audiences, String accessToken);
    TokenResponse exchangeToken(String uri, String audience, String accessToken);
}
