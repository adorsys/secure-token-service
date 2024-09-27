package de.adorsys.sts.token.tokenexchange;

import com.nimbusds.jose.proc.BadJOSEException;
import de.adorsys.sts.token.api.TokenResponse;

public interface TokenExchangeService {
    TokenResponse exchangeToken(TokenExchangeRequest tokenExchange) throws BadJOSEException;
}
