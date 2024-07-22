package de.adorsys.sts.resourceserver.service;

import com.google.common.util.concurrent.UncheckedExecutionException;
import com.nimbusds.jose.jwk.JWKSet;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachingKeyRetrieverServiceTest {

    public static final String AUDIENCE = "aud";
    private final KeyRetrieverService retrieverService = Mockito.mock(KeyRetrieverService.class);
    private final CachingKeyRetrieverService cachingKeyRetrieverService = new CachingKeyRetrieverService(
            retrieverService, 10, 10
    );

    @Test
    void retrievedExceptionThenOk() {
        when(retrieverService.retrieve(AUDIENCE))
                .thenThrow(new IllegalStateException("Couldn't retrieve remote jwk set from: url"))
                .thenReturn(new JWKSet());

        assertThrows(UncheckedExecutionException.class, () -> cachingKeyRetrieverService.retrieve(AUDIENCE));
        verify(retrieverService, times(1)).retrieve(AUDIENCE);

        JWKSet jwkSet1 = cachingKeyRetrieverService.retrieve(AUDIENCE);
        JWKSet jwkSet2 = cachingKeyRetrieverService.retrieve(AUDIENCE);

        verify(retrieverService, times(2)).retrieve(AUDIENCE);
        assertThat(jwkSet1).isEqualTo(jwkSet2);
        assertThat(jwkSet2.toString()).isEqualTo("{\"keys\":[]}");
    }
}
