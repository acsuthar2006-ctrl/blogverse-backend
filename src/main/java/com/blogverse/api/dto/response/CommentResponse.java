package com.blogverse.api.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record CommentResponse(
	Long id ,
	String content ,
	String authorName ,
	String authorEmail ,
	LocalDateTime createdDate ,
	LocalDateTime updatedDate ,
	boolean isReply ,
	Long parentCommentId ,
	String editToken ,
	List<CommentResponse> replies
) {
}
