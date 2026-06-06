package com.blogverse.api.service;

import com.blogverse.api.domain.entity.Author;
import com.blogverse.api.dto.request.CreatePostRequest;
import com.blogverse.api.dto.request.UpdatePostRequest;
import com.blogverse.api.dto.response.PostResponse;
import com.blogverse.api.dto.response.PostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
	PostResponse createPost (CreatePostRequest createPostRequest , Author author);
	PostResponse getPostBySlug(String slug);
	Page<PostSummaryResponse> findAllPostsByAuthor(String username, Pageable pageable);
	Page<PostSummaryResponse> findAllPublishedPosts(Pageable pageable);
	PostResponse updatePost(UpdatePostRequest updatePostRequest, String slug, Author author);
	void deletePost(String slug, Author author);
}
