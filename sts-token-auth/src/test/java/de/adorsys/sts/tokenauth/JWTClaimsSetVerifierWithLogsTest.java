package de.adorsys.sts.tokenauth;

import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTClaimsSetVerifierWithLogsTest {

    private JWTClaimsSetVerifierWithLogs<SecurityContext> underTest;
    @Mock
    private Clock clock;

    @BeforeEach
    public void setUp() {
        underTest = new JWTClaimsSetVerifierWithLogs<>(clock);
    }

    @Test
    void verify() {
        var jwtClaimsSetVerifierWithLogs = new JWTClaimsSetVerifierWithLogs<>(null);
        assertThrows(NullPointerException.class, () -> jwtClaimsSetVerifierWithLogs.verify(null, null));
    }

    @Test
    public void testVerify_throwsBadJWTException_whenJWTIsExpired() {
        Date exp = new Date(System.currentTimeMillis() - 60000);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().expirationTime(exp).build();
        when(clock.instant()).thenReturn(Instant.now());
        assertThrows(BadJWTException.class, () -> {
            underTest.verify(claimsSet, null);
        });
    }

    @Test
    public void testVerify_throwsBadJWTException_whenJWTIsNotBeforeNow() {
        Date nbf = new Date(System.currentTimeMillis() + 61000);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().notBeforeTime(nbf).build();
        when(clock.instant()).thenReturn(Instant.now());
        assertThrows(BadJWTException.class, () -> {
            underTest.verify(claimsSet, null);
        });
    }

}
