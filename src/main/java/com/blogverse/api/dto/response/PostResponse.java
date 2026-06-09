package com.blogverse.api.dto.response;

import com.blogverse.api.domain.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
	Long id ,
	String title ,
	String content ,
	String slug ,
	String summary ,
	PostStatus status ,
	AuthorSummary authorSummary ,
	LocalDateTime createdAt ,
	LocalDateTime publishedAt ,
	List<CategorySummary> categories,
	List<String> tags
) {
}
