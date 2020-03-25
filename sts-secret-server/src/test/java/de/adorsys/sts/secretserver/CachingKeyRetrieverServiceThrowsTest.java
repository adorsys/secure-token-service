package de.adorsys.sts.secretserver;

import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.resourceserver.service.CachingKeyRetrieverService;
import de.adorsys.sts.resourceserver.service.KeyRetrieverService;
import org.junit.Test;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CachingKeyRetrieverServiceThrowsTest {
    private KeyRetrieverService retrieverService = mock(KeyRetrieverService.class);
    @Test
    public void testThrows() {
        CachingKeyRetrieverService cachingKeyRetrieverService = new CachingKeyRetrieverService(
                retrieverService, 10, 10
        );
        when(retrieverService.retrieve("aud")).thenThrow(new RuntimeException("Ex"));
        try {
            cachingKeyRetrieverService.retrieve("aud");
        } catch (Exception e) {
            System.out.println("catched exception");
        }

        doReturn(new JWKSet()).when(retrieverService).retrieve("aud");

        cachingKeyRetrieverService.retrieve("aud");
    }
}
