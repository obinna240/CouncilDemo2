package com.sa.assist.error;

/**
 * Exception to be thrown in 'this shouldn't ever happen' type scenarios.
 */
public class LogicException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LogicException(String arg0) {
		super(arg0);
	}

	public LogicException(Throwable cause) {
		super(cause);
	}

	public LogicException(String message, Throwable cause) {
		super(message, cause);
	}

}
