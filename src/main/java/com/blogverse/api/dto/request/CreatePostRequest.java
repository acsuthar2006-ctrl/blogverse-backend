package com.blogverse.api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest(

	@NotBlank String title,
	@NotBlank String content ,
	String summary
) {}
