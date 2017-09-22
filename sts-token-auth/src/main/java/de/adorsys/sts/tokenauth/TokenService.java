package de.adorsys.sts.tokenauth;


import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import de.adorsys.sts.common.config.AuthServer;
import de.adorsys.sts.common.config.MultiAuthJWSKeySelector;
import org.adorsys.envutils.EnvProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TokenService {
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";

    private Map<String, AuthServer> authServers = new HashMap<>();


    private KeycloakTokenRolesParser keycloakTokenRolesParser = new KeycloakTokenRolesParser();

    private StringListRolesParser stringListRolesParser = new StringListRolesParser();

    @PostConstruct
    public void postConstruct(){
        String auth_servers_prop = EnvProperties.getEnvOrSysProp("AUTH_SERVER_NAMES", true);
        String[] auth_servers = StringUtils.split(auth_servers_prop);
        if(auth_servers!=null){
            for (String auth_server : auth_servers) {
                String auth_server_iss_url = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_ISS_URL", false);
                String auth_server_jwks_url = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_JWKS_URL", false);
                String auth_server_jwks_refresh_int = EnvProperties.getEnvOrSysProp(auth_server + "_AUTH_SERVER_JWKS_URL", "600");

                AuthServer authServer = new AuthServer(auth_server, auth_server_iss_url, auth_server_jwks_url);
                if(StringUtils.isNumeric(auth_server_jwks_refresh_int)){
                    int refreshIntervalSeconds = Integer.parseInt(auth_server_jwks_refresh_int);
                    authServer.setRefreshIntervalSeconds(refreshIntervalSeconds);
                }
                authServers.put(authServer.getIssUrl(), authServer);
            }
        }
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        // Accept only auth token.
        String token = request.getHeader(HEADER_STRING);
        if(token==null) return null;

        // Accepts only Bearer token
        if(!StringUtils.startsWithIgnoreCase(token, TOKEN_PREFIX)) return null;

        // Strip prefix
        token = StringUtils.substringAfterLast(token, " ");

        JWTClaimsSet jwtClaimsSet = checkBearerToken(token);
        // TODO log invalid token
        if(jwtClaimsSet==null) return null;

        // process roles
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = extractRoles(jwtClaimsSet);
        if(roles!=null){
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtClaimsSet.getSubject(), jwtClaimsSet, authorities);
        return authenticationToken;

    }

    public JWTClaimsSet checkBearerToken(String token) {

        JWTClaimsSet jwtClaimsSet = null;
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Check check algorithm
            JWSAlgorithm algorithm = signedJWT.getHeader().getAlgorithm();
            if(JWSAlgorithm.NONE.equals(algorithm)) return null;// TODO log no alg

            String issuer = signedJWT.getJWTClaimsSet().getIssuer();
            AuthServer authServer = authServers.get(issuer);

            // Accept only registered servers
            if(authServer==null) return null;

            MultiAuthJWSKeySelector<SecurityContext> jwsKeySelector = new MultiAuthJWSKeySelector<>(authServer);

            // Set up a JWT processor to parse the tokens and then check their signature
            // and validity time window (bounded by the "iat", "nbf" and "exp" claims)
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(jwsKeySelector);

            SecurityContext context = null;
            jwtClaimsSet = jwtProcessor.process(signedJWT, context);
        } catch (ParseException | BadJOSEException | JOSEException e) {
            // TODO log invalid token
            return null;
        }

        return jwtClaimsSet;
    }


    public List<String> extractRoles(JWTClaimsSet claimSet) {
        List<String> results = new ArrayList<>();

        stringListRolesParser.extractRoles(claimSet, "scp", results);
        stringListRolesParser.extractRoles(claimSet, "roles", results);

        keycloakTokenRolesParser.parseRoles(claimSet, results);

        return results;
    }

}