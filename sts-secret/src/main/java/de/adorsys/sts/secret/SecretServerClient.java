package de.adorsys.sts.secret;

import java.util.HashMap;
import java.util.Map;

public interface SecretServerClient {

    /**
     * Provides the decrypted BASE64 encoded secret for the user using the specified token.
     */
    default String getSecret(String token) {
        return getSecret(token, new HashMap<>());
    }

    /**
     * Provides the decrypted BASE64 encoded secret for the user using the specified token.
     */
    String getSecret(String token, Map<String, String> additionalHeaders);
}
