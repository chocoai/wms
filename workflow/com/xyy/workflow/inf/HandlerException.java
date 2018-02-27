package com.xyy.workflow.inf;

public class HandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3237420014302427756L;

	public HandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public HandlerException(String message) {
		super(message);
	}

	public HandlerException(Throwable cause) {
		super(cause);
	}

}
