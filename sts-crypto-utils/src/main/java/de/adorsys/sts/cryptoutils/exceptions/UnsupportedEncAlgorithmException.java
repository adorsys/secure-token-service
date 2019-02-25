package de.adorsys.sts.cryptoutils.exceptions;


public class UnsupportedEncAlgorithmException extends RuntimeException {
    private static final long serialVersionUID = -102550810645375099L;

    public UnsupportedEncAlgorithmException(String message) {
        super(message);
    }

    public UnsupportedEncAlgorithmException(String message, Throwable cause) {
        super(message, cause);
    }
}
