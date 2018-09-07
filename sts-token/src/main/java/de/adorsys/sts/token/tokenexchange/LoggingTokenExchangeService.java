package de.adorsys.sts.token.tokenexchange;

import de.adorsys.sts.token.api.TokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTokenExchangeService implements TokenExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingTokenExchangeService.class);

    private final TokenExchangeService decoratedTokenExchangeService;

    public LoggingTokenExchangeService(TokenExchangeService tokenExchangeService) {
        this.decoratedTokenExchangeService = tokenExchangeService;
    }

    @Override
    public TokenResponse exchangeToken(TokenExchangeRequest tokenExchange) {
        if(logger.isTraceEnabled()) logger.trace("exchangeToken started...");

        TokenResponse tokenResponse = decoratedTokenExchangeService.exchangeToken(tokenExchange);

        if(logger.isTraceEnabled()) logger.trace("exchangeToken finished.");

        return tokenResponse;
    }
}
