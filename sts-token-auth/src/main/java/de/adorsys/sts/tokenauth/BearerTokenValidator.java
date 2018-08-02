package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BearerTokenValidator {
    static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_KEY = "Authorization";

    private final AuthServersProvider authServersProvider;

    private final KeycloakTokenRolesParser keycloakTokenRolesParser = new KeycloakTokenRolesParser();
    private final StringListRolesParser stringListRolesParser = new StringListRolesParser();

    public BearerTokenValidator(AuthServersProvider authServersProvider) {
        this.authServersProvider = authServersProvider;
    }

    public BearerToken extract(String headerValue) {
        Optional<JWTClaimsSet> jwtClaimsSet = extractClaims(headerValue);
        if(jwtClaimsSet.isPresent()) {
            List<String> roles = extractRoles(jwtClaimsSet.get());

            return BearerToken.builder()
                    .token(headerValue)
                    .claims(jwtClaimsSet.get())
                    .isValid(true)
                    .roles(roles)
                    .build();
        }

        onInvalidToken(headerValue);

        return BearerToken.builder()
                .token(headerValue)
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
    }

    protected void onAlgorithmIsNone(String token) {
    }

    protected void onAuthServerIsNull(String token, String issuer) {
    }

    protected void onErrorWhileExtractClaims(String token, Throwable e) {
    }

    private Optional<JWTClaimsSet> extractClaims(String token) {
        Optional<JWTClaimsSet> jwtClaimsSet = Optional.empty();

        if(token == null) {
            onTokenIsNull();
            return jwtClaimsSet;
        }

        // Accepts only Bearer token
        if(!StringUtils.startsWithIgnoreCase(token, TOKEN_PREFIX)) return jwtClaimsSet;

        // Strip prefix
        String strippedToken = StringUtils.substringAfterLast(token, " ");

        try {
            SignedJWT signedJWT = SignedJWT.parse(strippedToken);

            // Check check algorithm
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if(JWSAlgorithm.NONE.equals(algorithm)) {
                onAlgorithmIsNone(token);
                return jwtClaimsSet;
            }

            String issuer = signedJWT.getJWTClaimsSet().getIssuer();
            AuthServer authServer = authServersProvider.get(issuer);

            // Accept only registered servers
            if(authServer == null){
                onAuthServerIsNull(token, issuer);
                return jwtClaimsSet;
            }

            MultiAuthJWSKeySelector<SecurityContext> jwsKeySelector = new MultiAuthJWSKeySelector<>(authServer);

            // Set up a JWT processor to parse the tokens and then check their signature
            // and validity time window (bounded by the "iat", "nbf" and "exp" claims)
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);

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
