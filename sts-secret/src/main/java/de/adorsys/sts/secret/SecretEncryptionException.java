package de.adorsys.sts.secret;

public class SecretEncryptionException extends RuntimeException {

    private static final long serialVersionUID = 6772836071562447738L;

    public SecretEncryptionException(String message) {
        super(message);
    }
}
