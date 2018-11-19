package de.adorsys.sts.token.authentication.securitycontext;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import de.adorsys.sts.keymanagement.service.SecretProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class SecurityContextSecretProvider implements SecretProvider {

    private final String audience;
    private final String secretClaimPropertyKey;
    private final ObjectMapperSPI objectMapper;

    public SecurityContextSecretProvider(
            @Value("${sts.audience-name}") String audience,
            @Value("${sts.secret-claim-property-key}") String secretClaimPropertyKey,
            ObjectMapperSPI objectMapper
    ) {
        this.audience = audience;
        this.objectMapper = objectMapper;
        this.secretClaimPropertyKey = secretClaimPropertyKey;
    }

    @Override
    public String get() {
        JWTClaimsSet claimsSet = (JWTClaimsSet) SecurityContextHolder.getContext().getAuthentication().getCredentials();
        String secretClaimsAsText = (String) claimsSet.getClaim(secretClaimPropertyKey);

        Map<String, String> secretClaims;
        try {
            secretClaims = objectMapper.readValue(secretClaimsAsText);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return secretClaims.get(audience);
    }
}
