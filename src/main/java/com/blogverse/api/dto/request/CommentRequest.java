package com.blogverse.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(
	@NotBlank @Size(max = 5000) String content,
	@NotBlank @Size(max = 255) String authorName,
	@NotBlank @Email @Size(max = 255) String authorEmail
) {}