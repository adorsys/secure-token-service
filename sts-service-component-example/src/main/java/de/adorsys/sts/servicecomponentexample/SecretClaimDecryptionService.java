package de.adorsys.sts.servicecomponentexample;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class SecretClaimDecryptionService {

    private static final TypeReference<Map<String, String>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, String>>() {
    };

    private final String audience;
    private final String secretClaimPropertyKey;

    private final DecryptionService decryptionService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecretClaimDecryptionService(
            @Value("${sts.audience-name}") String audience,
            @Value("${sts.secret-claim-property-key}") String secretClaimPropertyKey,
            DecryptionService decryptionService
    ) {
        this.audience = audience;
        this.secretClaimPropertyKey = secretClaimPropertyKey;
        this.decryptionService = decryptionService;
    }

    public String decryptSecretClaim() {
        Map<String, String> encryptedSecretClaims;
        try {
            encryptedSecretClaims = readSecretClaims();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String encryptedSecretClaim = encryptedSecretClaims.get(audience);

        return decryptionService.decrypt(encryptedSecretClaim);
    }

    private Map<String, String> readSecretClaims() throws IOException {
        JWTClaimsSet credentials = (JWTClaimsSet) SecurityContextHolder.getContext().getAuthentication().getCredentials();

        String secretClaim = (String)credentials.getClaim(secretClaimPropertyKey);

        return objectMapper.readValue(secretClaim, MAP_TYPE_REFERENCE);
    }
}
