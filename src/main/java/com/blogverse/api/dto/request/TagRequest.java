package com.blogverse.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for tag operations.
 * Note: Post creation uses List<String> for tag names directly(not implemented yet).
 * This DTO is reserved for future tag management endpoints
 * where additional fields may be required.
 */
public record TagRequest(
	@NotBlank @Size(max = 100) String name
) {}
