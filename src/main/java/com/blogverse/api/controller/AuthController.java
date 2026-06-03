package com.blogverse.api.controller;

import com.blogverse.api.dto.request.LoginRequest;
import com.blogverse.api.dto.request.RegisterRequest;
import com.blogverse.api.dto.response.ApiResponse;
import com.blogverse.api.dto.response.AuthResponse;
import com.blogverse.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AuthResponse>> register(
		@RequestBody @Valid RegisterRequest registerRequest
	) {
		AuthResponse response = authService.register(registerRequest);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(response, "Author registered successfully"));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
		@RequestBody @Valid LoginRequest loginRequest
	) {
		AuthResponse response = authService.login(loginRequest);

		return  ResponseEntity
			.ok(ApiResponse.success(response, "Author login successfully"));
	}
}
