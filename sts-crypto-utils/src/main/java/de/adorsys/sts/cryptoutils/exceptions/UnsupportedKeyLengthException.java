package de.adorsys.sts.cryptoutils.exceptions;


public class UnsupportedKeyLengthException extends RuntimeException {
	private static final long serialVersionUID = -102550810645375099L;

	public UnsupportedKeyLengthException(String message) {
		super(message);
	}

	public UnsupportedKeyLengthException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
