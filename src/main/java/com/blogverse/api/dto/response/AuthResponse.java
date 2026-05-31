package com.blogverse.api.dto.response;

import com.blogverse.api.domain.enums.Role;

public record AuthResponse(
		String token,
		String email,
		Role role) {
}