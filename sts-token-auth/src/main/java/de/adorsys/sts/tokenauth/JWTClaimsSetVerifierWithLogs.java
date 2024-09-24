package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Date;

@RequiredArgsConstructor
public class JWTClaimsSetVerifierWithLogs<C extends SecurityContext> implements JWTClaimsSetVerifier<C> {
    private final Logger logger = LoggerFactory.getLogger(JWTClaimsSetVerifierWithLogs.class);

    /**
     * The default maximum acceptable clock skew, in seconds (60).
     */
    private static final int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;
    private final Clock clock;

    @Override
    public void verify(JWTClaimsSet claimsSet, SecurityContext context) throws BadJWTException {
        final Date now = Date.from(clock.instant());

        final Date exp = claimsSet.getExpirationTime();

        if (exp != null && !DateUtils.isAfter(exp, now, DEFAULT_MAX_CLOCK_SKEW_SECONDS)) {
            String msg = "Expired JWT - expiration time claim (exp) is not after the current time";
            logger.error("{}: expiration time: {} now: {}", msg, exp, now);
            throw new BadJWTException(msg);
        }

        final Date nbf = claimsSet.getNotBeforeTime();

        if (nbf != null && !DateUtils.isBefore(nbf, now, DEFAULT_MAX_CLOCK_SKEW_SECONDS)) {
            String msg = "JWT before use time- not before claim (nbf) is after the current time";
            logger.error("{}: not before time: {} now: {}", msg, nbf, now);
            throw new BadJWTException(msg);
        }
    }
}
