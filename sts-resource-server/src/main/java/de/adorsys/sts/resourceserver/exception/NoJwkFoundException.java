package de.adorsys.sts.resourceserver.exception;

public class NoJwkFoundException extends RuntimeException {
    public NoJwkFoundException(String message) {
        super(message);
    }
}
