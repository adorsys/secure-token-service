package de.adorsys.sts.token.tokenexchange;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.service.EncryptionService;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import de.adorsys.sts.secret.EncryptedSecret;
import de.adorsys.sts.secret.Secret;
import de.adorsys.sts.secret.SecretRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TokenExchangeSecretClaimsService implements TokenExchangeClaimsService {
    private static final int BITS_PER_BYTES = 8;

    private final Integer secretLengthInBits;
    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;
    private final ResourceServerService resourceServerService;

    public TokenExchangeSecretClaimsService(
            Integer secretLengthInBits,
            SecretRepository secretRepository,
            EncryptionService encryptionService,
            ResourceServerService resourceServerService
    ) {
        this.secretLengthInBits = secretLengthInBits;
        this.secretRepository = secretRepository;
        this.encryptionService = encryptionService;
        this.resourceServerService = resourceServerService;
    }

    @Override
    public void extendClaims(JWTClaimsSet.Builder claimsBuilder, String[] audiences, String[] resources, String subject) {
        Map<String, String> encryptedSecretClaims = buildEncryptedSecretClaimsForAudiencesXorResources(audiences, resources, subject);
        claimsBuilder.claim(TokenExchangeConstants.SECRETS_CLAIM_KEY, encryptedSecretClaims);
    }

    private Map<String, String> buildEncryptedSecretClaimsForAudiencesXorResources(String[] audiences, String[] resources, String subject) {
        Map<String, String> encryptedSecretClaims = new HashMap<>();

        Map<String, ResourceServer> resourceServers = getResourceServersByAudiencesXorResources(audiences, resources);

        Secret secret = getSecretForSubject(subject);

        for(Map.Entry<String, ResourceServer> entry : resourceServers.entrySet()) {
            String audienceXorResource = entry.getKey();
            EncryptedSecret encryptedSecret = encryptSecretForAudience(audienceXorResource, secret);
            encryptedSecretClaims.put(audienceXorResource, encryptedSecret.getValue());
        }

        return encryptedSecretClaims;
    }

    private Map<String, ResourceServer> getResourceServersByAudiencesXorResources(String[] audiences, String[] resources) {
        Map<String, ResourceServer> resourceServers;

        if(audiences.length > 0) {
            resourceServers = resourceServerService.getForAudiences(audiences);
        } else if(resources.length > 0) {
            resourceServers = resourceServerService.getForResources(resources);
        } else {
            resourceServers = new HashMap<>();
        }

        return resourceServers;
    }

    private EncryptedSecret encryptSecretForAudience(String audience, Secret secret) {
        String encrypted = encryptionService.encryptFor(audience, secret.getValue());
        return new EncryptedSecret(encrypted);
    }

    private Secret getSecretForSubject(String subject) {
        Optional<Secret> maybeSecret = secretRepository.tryToGet(subject);
        return maybeSecret.orElseGet(() -> generateSecretFor(subject));
    }

    private Secret generateSecretFor(String subject) {
        Secret generatedSecret = Secret.generateRandom(secretLengthInBits / BITS_PER_BYTES);

        secretRepository.save(subject, generatedSecret);

        return generatedSecret;
    }
}
