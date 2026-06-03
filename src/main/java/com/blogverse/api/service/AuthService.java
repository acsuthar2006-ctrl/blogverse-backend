package com.blogverse.api.service;

import com.blogverse.api.dto.request.LoginRequest;
import com.blogverse.api.dto.request.RegisterRequest;
import com.blogverse.api.dto.response.AuthResponse;

public interface AuthService {
	AuthResponse register(RegisterRequest registerRequest);
	AuthResponse login(LoginRequest loginRequest);
}
