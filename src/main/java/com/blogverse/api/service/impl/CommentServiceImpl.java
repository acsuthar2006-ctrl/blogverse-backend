package com.blogverse.api.service.impl;

import com.blogverse.api.domain.entity.Comment;
import com.blogverse.api.domain.entity.Post;
import com.blogverse.api.dto.request.CommentReplyRequest;
import com.blogverse.api.dto.request.CommentRequest;
import com.blogverse.api.dto.request.CommentUpdateRequest;
import com.blogverse.api.dto.response.CommentResponse;
import com.blogverse.api.exception.ResourceNotFoundException;
import com.blogverse.api.repository.CommentRepository;
import com.blogverse.api.repository.PostRepository;
import com.blogverse.api.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;

	@Override
	@Transactional
	public CommentResponse createComment(Long postId, CommentRequest commentRequest) {

		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post Not Found"));

		Comment comment = Comment.builder()
				.content(commentRequest.content())
				.authorName(commentRequest.authorName())
				.authorEmail(commentRequest.authorEmail())
				.post(post)
				.parentComment(null)
				.build();

		return mapToResponse(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public CommentResponse replyComment(Long commentId, CommentReplyRequest commentReplyRequest,
			UserDetails userDetails) {
		boolean canReply = userDetails.getAuthorities().stream()
				.anyMatch(auth -> Objects.equals(auth.getAuthority(), "AUTHOR") ||
						Objects.equals(auth.getAuthority(), "ADMIN"));

		if (!canReply) {
			throw new RuntimeException("Only authors can reply to comments");
		}

		Comment parentComment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment Not Found"));

		Comment comment = Comment.builder()
				.content(commentReplyRequest.content())
				.authorName(commentReplyRequest.authorName())
				.authorEmail(commentReplyRequest.authorEmail())
				.post(parentComment.getPost())
				.parentComment(parentComment)
				.build();

		return mapToResponse(commentRepository.save(comment));
	}

	@Override
	public List<CommentResponse> findByPostId(Long postId) {

		postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post Not Found"));

		List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNull(postId);

		return comments.stream()
				.map(this::mapToResponseWithReplies)
				.toList();
	}

	@Override
	@Transactional
	public CommentResponse updateComment(Long commentId,
			CommentUpdateRequest commentUpdateRequest,
			String editToken,
			UserDetails userDetails) {

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new ResourceNotFoundException("Comment Not Found"));

		if (userDetails != null) {
			// Authenticated user: check ownership or admin role (no editToken needed)
			boolean isAuthor = Objects.equals(comment.getAuthorEmail(), userDetails.getUsername());
			boolean isAdmin = userDetails.getAuthorities().stream()
					.anyMatch(auth -> Objects.equals(auth.getAuthority(), "ADMIN"));

			if (!isAuthor && !isAdmin) {
				throw new RuntimeException("You can only edit your own comments");
			}
		} else {
			// Anonymous reader: must provide valid editToken
			if (!isTokenValid(commentId, editToken)) {
				throw new RuntimeException("Invalid edit token");
			}
		}

		comment.setContent(commentUpdateRequest.content());
		return mapToResponse(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public void deleteComment(Long commentId, String editToken, UserDetails userDetails) {

		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

		if (userDetails != null) {
			// Authenticated user: check ownership or admin role (no editToken needed)
			boolean isAuthor = Objects.equals(comment.getAuthorEmail(), userDetails.getUsername());
			boolean isAdmin = userDetails.getAuthorities().stream()
					.anyMatch(auth -> Objects.equals(auth.getAuthority(), "ADMIN"));

			if (!isAuthor && !isAdmin) {
				throw new RuntimeException("You don't have permission to delete this comment");
			}
		} else {
			// Anonymous reader: must provide valid editToken
			if (!isTokenValid(commentId, editToken)) {
				throw new RuntimeException("Invalid edit token");
			}
		}

		commentRepository.deleteById(commentId);
	}

	private String generateEditToken(Long commentId) {
		String tokenData = commentId + ":secret-key";
		return java.util.Base64.getEncoder()
				.encodeToString(tokenData.getBytes());
	}

	private boolean isTokenValid(Long commentId, String editToken) {
		return generateEditToken(commentId).equals(editToken);
	}

	private CommentResponse mapToResponse(Comment comment) {
		return new CommentResponse(
				comment.getId(),
				comment.getContent(),
				comment.getAuthorName(),
				comment.getAuthorEmail(),
				comment.getCreatedDate(),
				comment.getUpdatedDate(),
				comment.getParentComment() != null,
				comment.getParentComment() != null ? comment.getParentComment().getId() : null,
				generateEditToken(comment.getId()),
				List.of() // No nested replies in this response
		);
	}

	private CommentResponse mapToResponseWithReplies(Comment comment) {
		List<Comment> replies = commentRepository.findByParentCommentId(comment.getId());
		List<CommentResponse> nestedReplies = replies.stream()
				.map(this::mapToResponse)
				.toList();

		return new CommentResponse(
				comment.getId(),
				comment.getContent(),
				comment.getAuthorName(),
				comment.getAuthorEmail(),
				comment.getCreatedDate(),
				comment.getUpdatedDate(),
				false, // Top-level comments are never replies
				null,
				generateEditToken(comment.getId()),
				nestedReplies);
	}
}
