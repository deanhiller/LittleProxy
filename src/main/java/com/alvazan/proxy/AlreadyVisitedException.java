package com.alvazan.proxy;

public class AlreadyVisitedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyVisitedException() {
		// TODO Auto-generated constructor stub
	}

	public AlreadyVisitedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public AlreadyVisitedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public AlreadyVisitedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
