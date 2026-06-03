package com.blogverse.api.exception;

public class EmailAlreadyExistsException extends RuntimeException {

	public EmailAlreadyExistsException(String message) {
		super(message);
	}
}
