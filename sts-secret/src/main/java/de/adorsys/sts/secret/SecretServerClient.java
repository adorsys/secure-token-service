package de.adorsys.sts.secret;

public interface SecretServerClient {

    /**
     * Provides the decrypted BASE64 encoded secret for the user using the specified token.
     */
    String getSecret(String token);
}
