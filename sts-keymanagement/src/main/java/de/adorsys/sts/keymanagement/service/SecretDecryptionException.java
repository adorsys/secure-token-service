package de.adorsys.sts.keymanagement.service;

public class SecretDecryptionException extends RuntimeException {

    public SecretDecryptionException(String message) {
        super(message);
    }

    public SecretDecryptionException(Throwable cause) {
        super(cause);
    }
}
