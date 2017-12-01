package de.adorsys.sts.token.tokenexchange;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TokenExchangeRequest {

    private final String grantType;
    private final String[] resources;
    private final String subjectToken;
    private final String subjectTokenType;
    private final String actorToken;
    private final String actorTokenType;
    private final String issuer;
    private final String scope;
    private final String requestedTokenType;
    private final String[] audiences;
}
