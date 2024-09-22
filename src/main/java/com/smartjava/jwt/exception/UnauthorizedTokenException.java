package com.smartjava.jwt.exception;


public class UnauthorizedTokenException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthorizedTokenException(String message) {
        super(message);
    }
}
