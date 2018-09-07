package de.adorsys.sts.secretserverclient;

import de.adorsys.sts.token.authentication.BearerTokenAuthentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextTokenProvider implements TokenProvider {

    @Override
    public String get() {
        SecurityContext context = SecurityContextHolder.getContext();
        BearerTokenAuthentication authentication = (BearerTokenAuthentication)context.getAuthentication();

        return authentication.getToken();
    }
}
