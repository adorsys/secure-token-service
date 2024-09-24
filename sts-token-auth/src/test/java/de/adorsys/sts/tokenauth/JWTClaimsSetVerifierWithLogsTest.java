package de.adorsys.sts.tokenauth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JWTClaimsSetVerifierWithLogsTest {

    @org.junit.jupiter.api.Test
    void verify() {
        JWTClaimsSetVerifierWithLogs jwtClaimsSetVerifierWithLogs = new JWTClaimsSetVerifierWithLogs(null);
        assertThrows(NullPointerException.class, () -> jwtClaimsSetVerifierWithLogs.verify(null, null));
    }

    @Mock
    private Clock clock;

    private JWTClaimsSetVerifierWithLogs underTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new JWTClaimsSetVerifierWithLogs(clock);
    }

    @Test
    public void testVerify_throwsBadJWTException_whenJWTIsExpired() {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().expirationTime(new Date(System.currentTimeMillis() - 60000)).build();
        when(clock.instant()).thenReturn(Instant.now());
        assertThrows(BadJWTException.class, () -> {
            underTest.verify(claimsSet, null);
        });
    }

    @Test
    public void testVerify_throwsBadJWTException_whenJWTIsNotBeforeNow() {
        JWTClaimsSet claimsSet =
                new JWTClaimsSet.Builder().notBeforeTime(new Date(System.currentTimeMillis() + 600000000)).issueTime(new Date()).build();
        when(clock.instant()).thenReturn(Instant.now());
        assertThrows(BadJWTException.class, () -> {
            underTest.verify(claimsSet, null);
        });
    }

}
