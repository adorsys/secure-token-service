package de.adorsys.sts.token.authentication;

import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

@Service
public class TokenAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_KEY = "Authorization";

    private final BearerTokenValidator bearerTokenValidator;

    @Autowired
    public TokenAuthenticationService(BearerTokenValidator bearerTokenValidator) {
        this.bearerTokenValidator = bearerTokenValidator;
    }

    public Authentication getAuthentication(HttpServletRequest request) throws BadJOSEException {
        String headerValue = request.getHeader(HEADER_KEY);
        if (StringUtils.isBlank(headerValue)) {
            if (logger.isDebugEnabled())
                logger.debug("Header value '{}' is blank.", HEADER_KEY);
            return null;
        }

        // Accepts only Bearer token
        if (!StringUtils.startsWithIgnoreCase(headerValue, TOKEN_PREFIX)) {
            if (logger.isDebugEnabled())
                logger.debug("Header value does not start with '{}'.", TOKEN_PREFIX);
            return null;
        }

        // Strip prefix
        String strippedToken = StringUtils.substringAfterLast(headerValue, " ");

        BearerToken bearerToken = bearerTokenValidator.extract(strippedToken);

        if (!bearerToken.isValid()) {
            if (logger.isDebugEnabled())
                logger.debug("Token is not valid.");
            return null;
        }

        JWTClaimsSet jwtClaimsSet = bearerToken.getClaims();

        // process roles
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = bearerToken.getRoles();
        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        return new BearerTokenAuthentication(jwtClaimsSet.getSubject(), jwtClaimsSet, authorities, bearerToken.getToken());
    }
}
