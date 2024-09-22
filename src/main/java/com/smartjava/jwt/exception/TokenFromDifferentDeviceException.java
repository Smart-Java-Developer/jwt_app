package com.smartjava.jwt.exception;

public class TokenFromDifferentDeviceException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TokenFromDifferentDeviceException(String message) {
        super(message);
    }
}
