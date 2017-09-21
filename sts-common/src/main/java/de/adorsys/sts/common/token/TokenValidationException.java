package de.adorsys.sts.common.token;

public class TokenValidationException extends Exception {
	private static final long serialVersionUID = -2576469717846924984L;
	private final Object errorData;

	public TokenValidationException(Object errorData) {
		this.errorData = errorData;
	}

	public Object getErrorData() {
		return errorData;
	}
}
