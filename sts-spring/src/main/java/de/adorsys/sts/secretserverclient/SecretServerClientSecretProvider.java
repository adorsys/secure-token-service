package de.adorsys.sts.secretserverclient;

import de.adorsys.sts.keymanagement.service.SecretProvider;
import de.adorsys.sts.secret.SecretServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecretServerClientSecretProvider implements SecretProvider {

    private final TokenProvider tokenProvider;
    private final SecretServerClient secretServerClient;

    @Autowired
    public SecretServerClientSecretProvider(TokenProvider tokenProvider, SecretServerClient secretServerClient) {
        this.tokenProvider = tokenProvider;
        this.secretServerClient = secretServerClient;
    }

    @Override
    public String get() {
        String token = tokenProvider.get();
        return secretServerClient.getSecret(token);
    }
}
