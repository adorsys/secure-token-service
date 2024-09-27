package de.adorsys.sts.filter;

import com.nimbusds.jose.proc.BadJOSEException;
import de.adorsys.sts.token.authentication.TokenAuthenticationService;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final TokenAuthenticationService tokenAuthenticationService;

    @Override
    public void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        if (logger.isTraceEnabled()) logger.trace("doFilter start");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            if (logger.isDebugEnabled())
                logger.debug("Authentication is null. Try to get authentication from request...");

            try {
                authentication = tokenAuthenticationService.getAuthentication(request);
            } catch (BadJOSEException e) {
                response.setHeader("X-B3-TraceId", request.getHeader("X-B3-TraceId"));
                response.setHeader("X-B3-SpanId", request.getHeader("X-B3-SpanId"));
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid token - Token expired");
            }

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

        if (logger.isTraceEnabled()) logger.trace("doFilter end");
    }
}
