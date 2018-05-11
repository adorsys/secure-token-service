package de.adorsys.sts.resourceserver.service;

public class SecretEncryptionException extends RuntimeException {
	private static final long serialVersionUID = -1605381931967929560L;

	public SecretEncryptionException(Throwable cause) {
        super(cause);
    }

    public SecretEncryptionException(String message) {
        super(message);
    }
}
