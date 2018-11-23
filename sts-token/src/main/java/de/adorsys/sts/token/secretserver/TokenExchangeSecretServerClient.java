package de.adorsys.sts.token.secretserver;

import de.adorsys.sts.keymanagement.service.DecryptionService;
import de.adorsys.sts.secret.SecretServerClient;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.token.tokenexchange.TokenExchangeClient;
import de.adorsys.sts.token.tokenexchange.TokenExchangeConstants;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import net.minidev.json.JSONObject;

public class TokenExchangeSecretServerClient implements SecretServerClient {

    private final String audience;
    private final String secretServerUri;
    private final TokenExchangeClient tokenExchangeClient;
    private final BearerTokenValidator bearerTokenValidator;
    private final DecryptionService decryptionService;

    public TokenExchangeSecretServerClient(
            String audience,
            String secretServerUri,
            TokenExchangeClient tokenExchangeClient,
            BearerTokenValidator bearerTokenValidator,
            DecryptionService decryptionService
    ) {
        this.audience = audience;
        this.secretServerUri = secretServerUri;
        this.tokenExchangeClient = tokenExchangeClient;
        this.bearerTokenValidator = bearerTokenValidator;
        this.decryptionService = decryptionService;
    }

    @Override
    public String getSecret(String token) {
        TokenResponse tokenResponse = tokenExchangeClient.exchangeToken(secretServerUri, audience, token);
        String exchangedAccessToken = tokenResponse.getAccess_token();
        BearerToken bearerToken = bearerTokenValidator.extract(exchangedAccessToken);

        if(!bearerToken.isValid()) {
            throw new IllegalArgumentException("Exchanged token is invalid");
        }

        JSONObject claims = bearerToken.getClaims().toJSONObject();
        JSONObject encryptedSecrets = (JSONObject)claims.get(TokenExchangeConstants.SECRETS_CLAIM_KEY);

        String decryptedSecretForAudience = encryptedSecrets.get(audience).toString();

        return decryptionService.decrypt(decryptedSecretForAudience);
    }
}
