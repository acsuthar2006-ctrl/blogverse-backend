package com.blogverse.api.controller;

import com.blogverse.api.domain.entity.Author;
import com.blogverse.api.dto.request.CreatePostRequest;
import com.blogverse.api.dto.request.UpdatePostRequest;
import com.blogverse.api.dto.response.ApiResponse;
import com.blogverse.api.dto.response.PostResponse;
import com.blogverse.api.dto.response.PostSummaryResponse;
import com.blogverse.api.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@PostMapping
	public ResponseEntity<ApiResponse<PostResponse>> createPost(
		@RequestBody @Valid CreatePostRequest createPostRequest,
		@AuthenticationPrincipal Author author) {

		PostResponse postResponse = postService.createPost(createPostRequest, author);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(postResponse, "Post created successfully"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getAllPost(Pageable pageable) {

		Page<PostSummaryResponse> posts = postService.findAllPublishedPosts(pageable);
		return ResponseEntity
			.ok(ApiResponse.success(posts , "Posts found successfully"));
	}

	@GetMapping("/{slug}")
	public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable String slug) {

		PostResponse postResponse = postService.getPostBySlug(slug);
		return ResponseEntity
			.ok(ApiResponse.success(postResponse, "Post found successfully"));
	}

	@GetMapping("/author/{username}")
	public ResponseEntity<ApiResponse<Page<PostSummaryResponse>>> getPostByAuthor(
		@PathVariable String username,
		Pageable pageable) {

		Page<PostSummaryResponse> page = postService.findAllPostsByAuthor(username, pageable);
		return ResponseEntity
			.ok(ApiResponse.success(page, "Posts found successfully"));
	}

	@PutMapping("/{slug}")
	public ResponseEntity<ApiResponse<PostResponse>> updatePost(
		@PathVariable String slug,
		@RequestBody UpdatePostRequest updatePostRequest,
		@AuthenticationPrincipal Author author) {

		PostResponse postResponse = postService.updatePost(updatePostRequest, slug, author);
		return ResponseEntity
			.ok(ApiResponse.success(postResponse, "Post updated successfully"));
	}

	@DeleteMapping("/{slug}")
	public ResponseEntity<Void> deletePost(
		@PathVariable String slug,
		@AuthenticationPrincipal Author author) {

		postService.deletePost(slug, author);
		return ResponseEntity
			.noContent()
			.build();
	}

}
