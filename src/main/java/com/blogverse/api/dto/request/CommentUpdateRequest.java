package com.blogverse.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(
	@NotBlank @Size(max = 5000) String content
) {}