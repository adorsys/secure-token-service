package de.adorsys.sts.resourceserver;

public class ResourceServerException extends RuntimeException {
	private static final long serialVersionUID = -2576469717846924984L;
	private final Object errorData;

	public ResourceServerException(Object errorData) {
		super();
		this.errorData = errorData;
	}

	public Object getErrorData() {
		return errorData;
	}
}
