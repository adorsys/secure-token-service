package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BearerTokenValidator {
    private final Logger logger = LoggerFactory.getLogger(BearerTokenValidator.class);
    private final AuthServersProvider authServersProvider;

    private final KeycloakTokenRolesParser keycloakTokenRolesParser = new KeycloakTokenRolesParser();
    private final StringListRolesParser stringListRolesParser = new StringListRolesParser();

    public BearerTokenValidator(AuthServersProvider authServersProvider) {
        this.authServersProvider = authServersProvider;
    }

    public BearerToken extract(String token) {
        Optional<JWTClaimsSet> jwtClaimsSet = extractClaims(token);
        if(jwtClaimsSet.isPresent()) {
            List<String> roles = extractRoles(jwtClaimsSet.get());

            return BearerToken.builder()
                    .token(token)
                    .claims(jwtClaimsSet.get())
                    .isValid(true)
                    .roles(roles)
                    .build();
        } else {
            logger.error("Token has no claims");
        }

        onInvalidToken(token);

        return BearerToken.builder()
                .token(token)
                .isValid(false)
                .build();
    }

    protected void onInvalidToken(String headerValue) {
    }

    private List<String> extractRoles(JWTClaimsSet claims) {
        List<String> results = new ArrayList<>();

        stringListRolesParser.extractRoles(claims, "scp", results);
        stringListRolesParser.extractRoles(claims, "roles", results);

        keycloakTokenRolesParser.parseRoles(claims, results);

        return results;
    }

    protected void onTokenIsNull() {
        logger.error("token is null");
    }

    protected void onAlgorithmIsNone(String token) {
        logger.error("token without algorithm");
    }

    protected void onAuthServerIsNull(String token, String issuer) {
        logger.error("unknown/invalid issuer");
    }

    protected void onErrorWhileExtractClaims(String token, Throwable e) {
        logger.error("token parse exception");
    }

    private Optional<JWTClaimsSet> extractClaims(String token) {
        Optional<JWTClaimsSet> jwtClaimsSet = Optional.empty();

        if(token == null) {
            onTokenIsNull();
            return jwtClaimsSet;
        }

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Check check algorithm
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if (JWSAlgorithm.NONE.equals(algorithm)) {
                onAlgorithmIsNone(token);
                return jwtClaimsSet;
            }

            String issuer = signedJWT.getJWTClaimsSet().getIssuer();
            AuthServer authServer = authServersProvider.get(issuer);

            // Accept only registered servers
            if (authServer == null) {
                onAuthServerIsNull(token, issuer);
                return jwtClaimsSet;
            }

            MultiAuthJWSKeySelector<SecurityContext> jwsKeySelector = new MultiAuthJWSKeySelector<>(authServer);

            // Set up a JWT processor to parse the tokens and then check their signature
            // and validity time window (bounded by the "iat", "nbf" and "exp" claims)
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);
            JWTClaimsSetVerifierWithLogs<SecurityContext> claimsVerifier = new JWTClaimsSetVerifierWithLogs<>();
            jwtProcessor.setJWTClaimsSetVerifier(claimsVerifier);

            SecurityContext context = null;
            JWTClaimsSet jwtClaims = jwtProcessor.process(signedJWT, context);

            jwtClaimsSet = Optional.of(jwtClaims);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            onErrorWhileExtractClaims(token, e);
            return jwtClaimsSet;
        }

        return jwtClaimsSet;
    }
}
