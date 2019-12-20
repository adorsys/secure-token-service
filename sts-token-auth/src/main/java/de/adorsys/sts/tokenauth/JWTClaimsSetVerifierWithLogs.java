package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.nimbusds.jwt.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class JWTClaimsSetVerifierWithLogs<C extends SecurityContext>implements JWTClaimsSetVerifier<C> {
    private final Logger logger = LoggerFactory.getLogger(JWTClaimsSetVerifierWithLogs.class);

    /**
     * The default maximum acceptable clock skew, in seconds (60).
     */
    private static final int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;

    @Override
    public void verify(JWTClaimsSet claimsSet, SecurityContext context) throws BadJWTException {
        final Date now = new Date();

        final Date exp = claimsSet.getExpirationTime();

        if (exp != null && !DateUtils.isAfter(exp, now, DEFAULT_MAX_CLOCK_SKEW_SECONDS)) {
            String msg = "Expired JWT";
            logger.error(msg);
            throw new BadJWTException(msg);
        }

        final Date nbf = claimsSet.getNotBeforeTime();

        if (nbf != null && !DateUtils.isBefore(nbf, now, DEFAULT_MAX_CLOCK_SKEW_SECONDS)) {
            String msg = "JWT before use time";
            logger.error(msg);
            throw new BadJWTException(msg);
        }
    }
}
