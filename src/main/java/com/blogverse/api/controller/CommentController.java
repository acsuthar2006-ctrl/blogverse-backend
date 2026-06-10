package com.blogverse.api.controller;

import com.blogverse.api.dto.request.CommentReplyRequest;
import com.blogverse.api.dto.request.CommentRequest;
import com.blogverse.api.dto.request.CommentUpdateRequest;
import com.blogverse.api.dto.response.ApiResponse;
import com.blogverse.api.dto.response.CommentResponse;
import com.blogverse.api.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
	private final CommentService commentService;

	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<ApiResponse<CommentResponse>> createComment(
		@PathVariable Long postId,
		@Valid @RequestBody CommentRequest commentRequest
	) {
		CommentResponse commentResponse = commentService.createComment(postId, commentRequest);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(commentResponse , "Comment Created Successfully"));
	}

	@GetMapping("/posts/{postId}/comments")
	public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByPost(
		@PathVariable Long postId
	) {
		List<CommentResponse> comments = commentService.findByPostId(postId);

		return ResponseEntity
			.ok(ApiResponse.success(comments, "Comments Retrieved Successfully"));
	}

	@PostMapping("/comments/{commentId}/replies")
	public ResponseEntity<ApiResponse<CommentResponse>> replyToComment(
		@PathVariable Long commentId,
		@Valid @RequestBody CommentReplyRequest commentReplyRequest,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		CommentResponse replyResponse = commentService.replyComment(commentId, commentReplyRequest, userDetails);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(replyResponse, "Reply Created Successfully"));
	}

	@PutMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
		@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequest commentUpdateRequest,
		@RequestParam String editToken,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		CommentResponse updatedComment = commentService.updateComment(commentId, commentUpdateRequest, editToken, userDetails);

		return ResponseEntity
			.ok(ApiResponse.success(updatedComment, "Comment Updated Successfully"));
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(
		@PathVariable Long commentId,
		@RequestParam String editToken,
		@AuthenticationPrincipal UserDetails userDetails
	) {
		commentService.deleteComment(commentId, editToken, userDetails);

		return ResponseEntity
			.noContent()
			.build();
	}
}