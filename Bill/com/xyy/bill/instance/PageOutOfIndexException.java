package com.xyy.bill.instance;

public class PageOutOfIndexException extends Exception {

	private static final long serialVersionUID = -8786220638338075107L;

	public PageOutOfIndexException() {
		super();
	}

	public PageOutOfIndexException(String message, Throwable cause) {
		super(message, cause);
	}

	public PageOutOfIndexException(String message) {
		super(message);
	}

	public PageOutOfIndexException(Throwable cause) {
		super(cause);
	}

}
