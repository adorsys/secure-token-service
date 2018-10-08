package de.adorsys.sts.secret;

public class SecretReadException extends RuntimeException {
    private static final long serialVersionUID = -3515348046171117968L;

    public SecretReadException(String message) {
        super(message);
    }
}
