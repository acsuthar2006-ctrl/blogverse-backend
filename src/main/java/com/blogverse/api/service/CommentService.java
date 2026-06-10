package com.blogverse.api.service;

import com.blogverse.api.dto.request.CommentReplyRequest;
import com.blogverse.api.dto.request.CommentRequest;
import com.blogverse.api.dto.request.CommentUpdateRequest;
import com.blogverse.api.dto.response.CommentResponse;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface CommentService {
	CommentResponse createComment(Long postId, CommentRequest commentRequest);

	CommentResponse replyComment(Long commentId, CommentReplyRequest commentReplyRequest , UserDetails userDetails);

	List<CommentResponse> findByPostId(Long postId);

	CommentResponse updateComment(
		Long commentId,
		CommentUpdateRequest commentUpdateRequest,
		String editToken,
		UserDetails userDetails
	);

	void deleteComment(Long commentId , String editToken, UserDetails userDetails);
}
