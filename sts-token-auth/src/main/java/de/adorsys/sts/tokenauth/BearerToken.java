package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class BearerToken {
    private final String token;
    private final JWTClaimsSet claims;
    private final List<String> roles;
    private final boolean isValid;
}
