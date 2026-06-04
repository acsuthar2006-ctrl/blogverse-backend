package com.blogverse.api.dto.request;

import com.blogverse.api.domain.enums.PostStatus;

public record UpdatePostRequest(
	String title ,
	String content ,
	String summary ,
	PostStatus status
) {}
