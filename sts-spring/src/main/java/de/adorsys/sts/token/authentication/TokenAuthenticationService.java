package de.adorsys.sts.token.authentication;


import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.tokenauth.BearerToken;
import de.adorsys.sts.tokenauth.BearerTokenValidator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private final BearerTokenValidator bearerTokenValidator;

    @Autowired
    public TokenAuthenticationService(BearerTokenValidator bearerTokenValidator) {
        this.bearerTokenValidator = bearerTokenValidator;
    }

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(BearerTokenValidator.HEADER_KEY);
        if(StringUtils.isBlank(token)) {
            if(logger.isDebugEnabled()) logger.debug("Token is blank.");
            return null;
        }
        
        BearerToken bearerToken = bearerTokenValidator.extract(token);

        if (!bearerToken.isValid()) {
            if(logger.isDebugEnabled()) logger.debug("Token {} is not valid.", token);
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

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtClaimsSet.getSubject(), jwtClaimsSet, authorities);
        return authenticationToken;
    }
}
