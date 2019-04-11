package de.adorsys.sts.token.authentication;

import de.adorsys.sts.tokenauth.AuthServersProvider;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingBearerTokenValidator extends BearerTokenValidator {
    private final Logger logger = LoggerFactory.getLogger(LoggingBearerTokenValidator.class);

    public LoggingBearerTokenValidator(AuthServersProvider authServersProvider) {
        super(authServersProvider);
    }

    @Override
    protected void onInvalidToken(String headerValue) {
        if(logger.isDebugEnabled()) logger.debug("Token in header is invalid");
    }

    @Override
    protected void onTokenIsNull() {
        if(logger.isDebugEnabled()) logger.debug("Token is null");
    }

    @Override
    protected void onAlgorithmIsNone(String token) {
        if(logger.isDebugEnabled()) logger.debug("Token header alg is NONE");
    }

    @Override
    protected void onAuthServerIsNull(String token, String issuer) {
        if(logger.isDebugEnabled()) logger.debug("Auth server with issuer {} for token not allowed", issuer);
    }

    @Override
    protected void onErrorWhileExtractClaims(String token, Throwable e) {
        if(logger.isDebugEnabled()) logger.debug("Error occured while extracting claims from token", e);
    }
}
