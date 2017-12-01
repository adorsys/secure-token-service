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

public class BearerToken {
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";

    private final EnvironmentVariablesAuthServersProvider authServersProvider = new EnvironmentVariablesAuthServersProvider();

    private final KeycloakTokenRolesParser keycloakTokenRolesParser = new KeycloakTokenRolesParser();
    private final StringListRolesParser stringListRolesParser = new StringListRolesParser();

    private final String token;

    private JWTClaimsSet claims;


    public BearerToken(String token) {
        this.token = token;
    }

    public boolean isValid() {
        return tryToGetClaims().isPresent();
    }

    public final JWTClaimsSet getClaims() {
        Optional<JWTClaimsSet> claims = tryToGetClaims();
        return claims.orElseThrow(() -> new IllegalStateException("claims cannot be parsed"));
    }

    private Optional<JWTClaimsSet> tryToGetClaims() {
        if(claims == null) {
            Optional<JWTClaimsSet> extractedClaims = extractClaims();

            extractedClaims.ifPresent(jwtClaimsSet -> this.claims = jwtClaimsSet);
        }

        return Optional.ofNullable(claims);
    }

    public List<String> extractRoles() {
        List<String> results = new ArrayList<>();

        JWTClaimsSet claims = getClaims();

        stringListRolesParser.extractRoles(claims, "scp", results);
        stringListRolesParser.extractRoles(claims, "roles", results);

        keycloakTokenRolesParser.parseRoles(claims, results);

        return results;
    }

    private Optional<JWTClaimsSet> extractClaims() {
        Optional<JWTClaimsSet> jwtClaimsSet = Optional.empty();

        if(token==null) return jwtClaimsSet;

        // Accepts only Bearer token
        if(!StringUtils.startsWithIgnoreCase(token, TOKEN_PREFIX)) return jwtClaimsSet;

        // Strip prefix
        String strippedToken = StringUtils.substringAfterLast(token, " ");

        try {
            SignedJWT signedJWT = SignedJWT.parse(strippedToken);

            // Check check algorithm
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if(JWSAlgorithm.NONE.equals(algorithm)) return jwtClaimsSet;// TODO log no alg

            String issuer = signedJWT.getJWTClaimsSet().getIssuer();
            AuthServer authServer = authServersProvider.get(issuer);

            // Accept only registered servers
            if(authServer==null) return jwtClaimsSet;

            MultiAuthJWSKeySelector<SecurityContext> jwsKeySelector = new MultiAuthJWSKeySelector<>(authServer);

            // Set up a JWT processor to parse the tokens and then check their signature
            // and validity time window (bounded by the "iat", "nbf" and "exp" claims)
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);

            SecurityContext context = null;
            JWTClaimsSet jwtClaims = jwtProcessor.process(signedJWT, context);

            jwtClaimsSet = Optional.of(jwtClaims);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            // TODO log invalid token
            return jwtClaimsSet;
        }

        return jwtClaimsSet;
    }
}
