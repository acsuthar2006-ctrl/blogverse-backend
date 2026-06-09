package com.blogverse.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreatePostRequest(

	@NotBlank String title,
	@NotBlank String content ,
	String summary ,
	List<String> categories,
	@Size(max = 5) List<String> tags
) {
	public CreatePostRequest {
		tags = tags != null ? tags : List.of();
		categories = categories != null ? categories : List.of();
	}
}
