package de.adorsys.sts.resourceserver.service;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nimbusds.jose.jwk.JWKSet;
import java.util.concurrent.TimeUnit;
public class CachingKeyRetrieverService implements KeyRetrieverService {
    private final LoadingCache<String, JWKSet> jwkSets;
    public CachingKeyRetrieverService(
            KeyRetrieverService keyRetrieverService,
            int maximumSize,
            int expireAfterAccessInMinutes
    ) {
        jwkSets = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccessInMinutes, TimeUnit.MINUTES)
                .build(new CacheLoader<String, JWKSet>() {
                    @Override
                    public JWKSet load(String audience) throws Exception {
                        return keyRetrieverService.retrieve(audience);
                    }
                });
    }
    @Override
    public JWKSet retrieve(String audience) {
        try {
            return jwkSets.getUnchecked(audience);
        } catch (RuntimeException ex) {
            jwkSets.invalidate(audience);
            throw ex;
        }
    }
}
