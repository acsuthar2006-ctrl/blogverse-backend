package com.blogverse.api.dto.response;

public record CategoryResponse(
	Long id,
	String name ,
	String slug ,
	String description
) {
}
