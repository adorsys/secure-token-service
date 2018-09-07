package de.adorsys.sts.token.tokenexchange;

import de.adorsys.sts.token.api.TokenResponse;

public interface TokenExchangeService {
    TokenResponse exchangeToken(TokenExchangeRequest tokenExchange);
}
