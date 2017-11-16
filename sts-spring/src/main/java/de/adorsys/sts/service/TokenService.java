package de.adorsys.sts.service;


import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.tokenauth.BearerToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class TokenService {
    static final String HEADER_STRING = "Authorization";

    public Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        BearerToken bearerToken = new BearerToken(token);

        // TODO log invalid token
        if (!bearerToken.isValid()) return null;

        JWTClaimsSet jwtClaimsSet = bearerToken.getClaims();

        // process roles
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = bearerToken.extractRoles();
        if (roles != null) {
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority(role));
            }
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jwtClaimsSet.getSubject(), jwtClaimsSet, authorities);
        return authenticationToken;
    }
}
