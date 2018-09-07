package de.adorsys.sts.token.tokenexchange;

import com.nimbusds.jwt.JWTClaimsSet;

public interface TokenExchangeClaimsService {
    void extendClaims(JWTClaimsSet.Builder claimsBuilder, String[] audiences, String[] resources, String subject);
}
