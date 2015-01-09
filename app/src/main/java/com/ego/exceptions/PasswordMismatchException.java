package com.ego.exceptions;

public class PasswordMismatchException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Parameterless Constructor
	public PasswordMismatchException () {
	}

	// Constructor that accepts a message
	public PasswordMismatchException (String message) {
		super(message);
	}
}
