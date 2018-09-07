package de.adorsys.sts.token.tokenexchange.client;

import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoggingTokenExchangeClient implements TokenExchangeClient {
    private static final Logger logger = LoggerFactory.getLogger(LoggingTokenExchangeClient.class);

    private final TokenExchangeClient decoratedTokenExchangeClient;

    public LoggingTokenExchangeClient(TokenExchangeClient tokenExchangeClient) {
        this.decoratedTokenExchangeClient = tokenExchangeClient;
    }

    @Override
    public TokenResponse exchangeToken(String uri, List<String> audiences, String accessToken) {
        if(logger.isTraceEnabled()) logger.trace("exchangeToken for audiences start...");

        TokenResponse tokenResponse = decoratedTokenExchangeClient.exchangeToken(uri, audiences, accessToken);

        if(logger.isTraceEnabled()) logger.trace("exchangeToken for audiences finish.");

        return tokenResponse;
    }

    @Override
    public TokenResponse exchangeToken(String uri, String audience, String accessToken) {
        if(logger.isTraceEnabled()) logger.trace("exchangeToken for audience start...");

        TokenResponse tokenResponse = decoratedTokenExchangeClient.exchangeToken(uri, audience, accessToken);

        if(logger.isTraceEnabled()) logger.trace("exchangeToken for audience finish.");

        return tokenResponse;
    }
}
