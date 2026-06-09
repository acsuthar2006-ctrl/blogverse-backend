package com.blogverse.api.dto.request;

import com.blogverse.api.domain.enums.PostStatus;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdatePostRequest(
	String title ,
	String content ,
	String summary ,
	PostStatus status ,
	List<String> categories,
	@Size(max = 5) List<String> tags
) {
	public UpdatePostRequest {
		tags = tags != null ? tags : List.of();
		categories = categories != null ? categories : List.of();
	}
}
