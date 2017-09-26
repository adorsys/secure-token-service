package de.adorsys.sts.resourceserver.service;

public class SecretEncryptionException extends RuntimeException {

    public SecretEncryptionException(Throwable cause) {
        super(cause);
    }

    public SecretEncryptionException(String message) {
        super(message);
    }
}
