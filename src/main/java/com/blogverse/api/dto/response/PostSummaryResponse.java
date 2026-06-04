package com.blogverse.api.dto.response;

import com.blogverse.api.domain.enums.PostStatus;

import java.time.LocalDateTime;

public record PostSummaryResponse(
	Long id ,
	String title ,
	String slug ,
	String summary ,
	PostStatus status ,
	AuthorSummary authorSummary ,
	LocalDateTime createdAt ,
	LocalDateTime publishedAt
) {
}
