package de.adorsys.sts.cryptoutils.exceptions;


public class KeyExtractionException extends RuntimeException {
	private static final long serialVersionUID = -102550810645375099L;

	public KeyExtractionException(String message) {
		super(message);
	}

	public KeyExtractionException(String message, Throwable cause) {
		super(message, cause);
	}
}
