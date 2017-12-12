package de.adorsys.sts.token.tokenexchange;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessor;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.JwtClaimSetHelper;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenResponse;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.adorsys.jjwk.serverkey.KeyAndJwk;
import org.adorsys.jjwk.serverkey.KeyConverter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TokenExchangeService {

    private final ResourceServerProcessor resourceServerProcessor;
    private final KeyManagementService keyManager;
    private final BearerTokenValidator bearerTokenValidator;

    public TokenExchangeService(ResourceServerProcessor resourceServerProcessor, KeyManagementService keyManager, BearerTokenValidator bearerTokenValidator) {
        this.resourceServerProcessor = resourceServerProcessor;
        this.keyManager = keyManager;
        this.bearerTokenValidator = bearerTokenValidator;
    }

    public TokenResponse exchangeToken(TokenExchangeRequest tokenExchange) {
        return exchangeToken(
                tokenExchange.getGrantType(),
                tokenExchange.getResources(),
                tokenExchange.getSubjectToken(),
                tokenExchange.getSubjectTokenType(),
                tokenExchange.getActorToken(),
                tokenExchange.getActorTokenType(),
                tokenExchange.getIssuer(),
                tokenExchange.getScope(),
                tokenExchange.getRequestedTokenType(),
                tokenExchange.getAudiences()
                );
    }

    public TokenResponse exchangeToken(
            String grant_type,
            String[] resources,
            String subject_token,
            String subject_token_type,
            String actor_token,
            String actor_token_type,
            String issuer,
            String scope,
            String requested_token_type,
            String[] audiences
    )
            throws InvalidParameterException, MissingParameterException, TokenValidationException {
        // Validate input parameters.
        if (!StringUtils.equals("urn:ietf:params:oauth:grant-type:token-exchange", grant_type)) {
            throw new InvalidParameterException("Request parameter grant_type is missing or does not carry the value urn:ietf:params:oauth:grant-type:token-exchange. See https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08#section-2.1");
        }

        if (StringUtils.isBlank(subject_token)) {
            throw new MissingParameterException("subject_token");
        }

        if (StringUtils.isBlank(subject_token_type)) {
            throw new MissingParameterException("subject_token_type");
        }

        // If requested token type is not null, then the value must be urn:ietf:params:oauth:token-type:jwt
        if (StringUtils.isNotBlank(requested_token_type) && !StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", requested_token_type)) {
            throw new InvalidParameterException("Request parameter requested_token_type must be left blank or carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types are supported by this version");
        }

        if (!StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", subject_token_type)) {
            throw new InvalidParameterException("Request parameter subject_token_type is missing or does not carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types can be consumed by this version");
        }

        BearerToken subjectBearerToken = bearerTokenValidator.extract(subject_token);

        if (!subjectBearerToken.isValid()) {
            String tokenName = "subject_token";
            throw new TokenValidationException("Token in field " + tokenName + " does not seam to be a valid token");
        }

        JWTClaimsSet actorTokenClaim = null;
        if (StringUtils.isNotBlank(actor_token)) {
            // If actor token is not null, then the value of the actor_token_type must be urn:ietf:params:oauth:token-type:jwt
            if (!StringUtils.equals("urn:ietf:params:oauth:token-type:jwt", actor_token_type)) {
                throw new InvalidParameterException("The conditional parameter actor_token_type must be set when actor_token is sent and carry the value urn:ietf:params:oauth:token-type:jwt. Only JWT token types are supported by this version");
            }

            BearerToken actorBearerToken = bearerTokenValidator.extract(actor_token);

            if (!actorBearerToken.isValid()) {
                String tokenName = "actor_token";

                throw new TokenValidationException("Token in field " + tokenName + " does not seam to be a valid token");
            }
        }

        JWTClaimsSet subjectTokenClaim = subjectBearerToken.getClaims();

        JWTClaimsSet.Builder claimSetBuilder = new JWTClaimsSet.Builder();
        claimSetBuilder = claimSetBuilder.subject(subjectTokenClaim.getSubject())
                .expirationTime(subjectTokenClaim.getExpirationTime())
                .issuer(issuer)
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(subjectTokenClaim.getNotBeforeTime())
                .claim("typ", "Bearer")
                .claim("acr", subjectTokenClaim.getClaim("acr"))
                .claim("role", "USER");
// preferred_username

        // Dealing with scope
        List<String> existingScope = subjectBearerToken.getRoles();
        List<String> newScopeList = existingScope;
        if (StringUtils.isNotBlank(scope)) {
            newScopeList = new ArrayList<>();
            String[] scopes = StringUtils.split(scope);
            for (String scopeStr : scopes) {
                if (existingScope.contains(scopeStr)) {
                    newScopeList.add(scopeStr);
                }
            }
        }
        if (!newScopeList.isEmpty()) {
            claimSetBuilder.claim("scp", newScopeList);
        }

        // TODO produce user data service from controller
        List<ResourceServerAndSecret> processedResources = resourceServerProcessor.processResources(audiences, resources);
        // Resources or audiances
        claimSetBuilder = JwtClaimSetHelper.handleResources(claimSetBuilder, processedResources);

        for (ResourceServerAndSecret resourceServerAndSecret : processedResources) {
            if (!resourceServerAndSecret.hasEncryptedSecret()) continue;
            if (StringUtils.isNotBlank(resourceServerAndSecret.getResourceServer().getUserSecretClaimName())) {
                claimSetBuilder.claim(resourceServerAndSecret.getResourceServer().getUserSecretClaimName(), resourceServerAndSecret.getEncryptedSecret());
            }
        }

        // Actor Token
        if (actorTokenClaim != null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sub", actorTokenClaim.getSubject());
            hashMap.put("iss", actorTokenClaim.getIssuer());
            Object nestedActor = actorTokenClaim.getClaim("act");
            if (nestedActor != null) {
                hashMap.put("act", nestedActor);
            }
            claimSetBuilder = claimSetBuilder.claim("act", hashMap);
        }

        JWTClaimsSet jwtClaimsSet = claimSetBuilder.build();

        KeyAndJwk randomKey = keyManager.getKeyMap().randomSignKey();
        JWSAlgorithm jwsAlgo = KeyConverter.getJWSAlgo(randomKey);

        JWSHeader jwsHeader = new JWSHeader.Builder(jwsAlgo)
                .type(JOSEObjectType.JWT)
                .keyID(randomKey.jwk.getKeyID())
                .build();

        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
        try {
            signedJWT.sign(KeyConverter.findSigner(randomKey));
        } catch (JOSEException e) {
            throw new IllegalStateException(e);
        }

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccess_token(signedJWT.serialize());
        tokenResponse.setIssued_token_type(TokenResponse.ISSUED_TOKEN_TYPE_ACCESS_TOKEN);
        tokenResponse.setToken_type(TokenResponse.TOKEN_TYPE_BEARER);
        int expires_in = (int) ((jwtClaimsSet.getExpirationTime().getTime() - new Date().getTime()) / 1000);
        tokenResponse.setExpires_in(expires_in);
        String newScopeSpaceSparated = null;
        if (newScopeList != null) {
            for (String scopeStr : newScopeList) {
                if (newScopeSpaceSparated == null) {
                    newScopeSpaceSparated = scopeStr;
                } else {
                    newScopeSpaceSparated = newScopeSpaceSparated + " " + scopeStr;
                }
            }
        }
        tokenResponse.setScope(newScopeSpaceSparated);

        return tokenResponse;
    }
}
