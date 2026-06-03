package com.blogverse.api.exception;

import com.blogverse.api.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<?>> handleEmailExists(
		EmailAlreadyExistsException ex) {
		return ResponseEntity
			.status(HttpStatus.CONFLICT)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<?>> handleNotFound(
		ResourceNotFoundException ex) {
		return ResponseEntity
			.status(HttpStatus.NOT_FOUND)
			.body(ApiResponse.error(ex.getMessage()));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<?>> handleBadCredentials(
		BadCredentialsException ex) {
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(ApiResponse.error("Invalid email or password"));
	}
}
