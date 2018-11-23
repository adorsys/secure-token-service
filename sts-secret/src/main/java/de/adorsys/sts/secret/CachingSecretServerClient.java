package de.adorsys.sts.secret;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

public class CachingSecretServerClient implements SecretServerClient {

    private final LoadingCache<String, String> secrets;

    public CachingSecretServerClient(
            SecretServerClient decoratedSecretServerClient,
            int maximumSize,
            int expireAfterAccessInMinutes
    ) {
        secrets = CacheBuilder.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccessInMinutes, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String token) throws Exception {
                        return decoratedSecretServerClient.getSecret(token);
                    }
                });
    }

    @Override
    public String getSecret(String token) {
        return secrets.getUnchecked(token);
    }
}
