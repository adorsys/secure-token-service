package de.adorsys.sts.token.passwordgrant;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.adorsys.sts.common.model.KeyAndJwk;
import de.adorsys.sts.cryptoutils.KeyConverter;
import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import de.adorsys.sts.keymanagement.service.KeyManagementService;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;
import de.adorsys.sts.resourceserver.processing.ResourceServerProcessorService;
import de.adorsys.sts.token.InvalidParameterException;
import de.adorsys.sts.token.JwtClaimSetHelper;
import de.adorsys.sts.token.MissingParameterException;
import de.adorsys.sts.token.api.TokenResponse;

public class PasswordGrantService {

    private final KeyManagementService keyManager;

    private final ResourceServerProcessorService resourceServerProcessorService;

	private ObjectMapperSPI mapper;

    public PasswordGrantService(
            KeyManagementService keyManager,
            ResourceServerProcessorService resourceServerProcessorService,
            ObjectMapperSPI mapper
    ) {
        this.keyManager = keyManager;
        this.resourceServerProcessorService = resourceServerProcessorService;
        this.mapper = mapper;
    }

    public TokenResponse passwordGrant(
            String grant_type,
            String[] resources,
            String[] audiences,
            String issuer,
            String scope,
            String username,
            String password
    ) throws InvalidParameterException, MissingParameterException {
        // Validate input parameters.
        if (!StringUtils.equals("password", grant_type)) {
            throw new InvalidParameterException("Request parameter grant_type is missing or does not carry the value password. See https://tools.ietf.org/html/rfc6749#section-4.3.1");
//            return ResponseUtils.invalidParam("Request parameter grant_type is missing or does not carry the value password. See https://tools.ietf.org/html/rfc6749#section-4.3.1");
        }

        if (StringUtils.isBlank(username)) {
            throw new MissingParameterException("username");
        }

        if (StringUtils.isBlank(password)) {
            throw new MissingParameterException("password");
        }

        JWTClaimsSet.Builder claimSetBuilder = new JWTClaimsSet.Builder();
        claimSetBuilder = claimSetBuilder.subject(username)
                .expirationTime(DateUtils.addMinutes(new Date(), 5))
                .issuer(issuer)
                .issueTime(new Date())
                .jwtID(UUID.randomUUID().toString())
                .notBeforeTime(new Date())
                .claim("typ", "Bearer")
                .claim("role", "USER");

        List<ResourceServerAndSecret> processedResources = resourceServerProcessorService.processResources(audiences, resources, username, password);

//        for (ResourceServerAndSecret resourceServerAndSecret : processedResources) {
//            if (!resourceServerAndSecret.hasEncryptedSecret()) continue;
//            claimSetBuilder.claim(resourceServerAndSecret.getResourceServer().getUserSecretClaimName(), resourceServerAndSecret.getEncryptedSecret());
//        }

        // Resources or audiances
        claimSetBuilder = JwtClaimSetHelper.handleResources(claimSetBuilder, processedResources, mapper);

        JWTClaimsSet jwtClaimsSet = claimSetBuilder.build();

        KeyAndJwk randomKey = keyManager.randomSignKey();
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

        return tokenResponse;
    }
}
