package de.adorsys.sts.token.tokenexchange;

import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingTokenExchangeClaimsService implements TokenExchangeClaimsService {
    private static final Logger logger = LoggerFactory.getLogger(LoggingTokenExchangeClaimsService.class);

    private final TokenExchangeClaimsService decoratedTokenExchangeClaimsService;

    public LoggingTokenExchangeClaimsService(TokenExchangeClaimsService tokenExchangeClaimsService) {
        this.decoratedTokenExchangeClaimsService = tokenExchangeClaimsService;
    }

    @Override
    public void extendClaims(JWTClaimsSet.Builder claimsBuilder, String[] audiences, String[] resources, String subject) {
        if(logger.isTraceEnabled()) logger.trace("extendClaims started...");

        decoratedTokenExchangeClaimsService.extendClaims(claimsBuilder, audiences, resources, subject);

        if(logger.isTraceEnabled()) logger.trace("extendClaims finished.");
    }
}
