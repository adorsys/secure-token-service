package de.adorsys.sts.token.secretserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.secret.SecretServerClient;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;

import java.util.Map;

public class TokenExchangeSecretServerClient implements SecretServerClient {

    private final String audience;
    private final String secretServerUri;
    private final TokenExchangeClient tokenExchangeClient;
    private final BearerTokenValidator bearerTokenValidator;
    private final DecryptionService decryptionService;

    private final Map<String, String> customHeaders;

    public TokenExchangeSecretServerClient(
            String audience,
            String secretServerUri,
            TokenExchangeClient tokenExchangeClient,
            BearerTokenValidator bearerTokenValidator,
            DecryptionService decryptionService,
            Map<String, String> customHeaders
    ) {
        this.audience = audience;
        this.secretServerUri = secretServerUri;
        this.tokenExchangeClient = tokenExchangeClient;
        this.bearerTokenValidator = bearerTokenValidator;
        this.decryptionService = decryptionService;
        this.customHeaders = customHeaders;
    }

    @Override
    public String getSecret(String token) {
        TokenResponse tokenResponse = tokenExchangeClient.exchangeToken(secretServerUri, audience, token, customHeaders);
        String exchangedAccessToken = tokenResponse.getAccess_token();
        BearerToken bearerToken = bearerTokenValidator.extract(exchangedAccessToken);

        if (!bearerToken.isValid()) {
            throw new IllegalArgumentException("Exchanged token is invalid");
        }

        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> jsonObject = bearerToken.getClaims().toJSONObject();
        JsonNode claims = mapper.convertValue(jsonObject, JsonNode.class);

        JsonNode encryptedSecrets = claims.get(TokenExchangeConstants.SECRETS_CLAIM_KEY);

        String decryptedSecretForAudience = encryptedSecrets.get(audience).asText();

        return decryptionService.decrypt(decryptedSecretForAudience);
    }
}
