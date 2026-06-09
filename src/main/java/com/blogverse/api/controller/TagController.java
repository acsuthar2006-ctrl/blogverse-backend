package com.blogverse.api.controller;

import com.blogverse.api.dto.response.ApiResponse;
import com.blogverse.api.dto.response.TagResponse;
import com.blogverse.api.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {

	private final TagService tagService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<TagResponse>>> getTags() {
		List<TagResponse> tagResponses = tagService.findAllTags();

		return ResponseEntity
				.ok(ApiResponse.success(tagResponses, "Tags found"));
	}

	@GetMapping("/{slug}")
	public ResponseEntity<ApiResponse<TagResponse>> getTag(@PathVariable String slug) {

		TagResponse tagResponse = tagService.findBySlug(slug);
		return ResponseEntity
				.ok(ApiResponse.success(tagResponse, "Tag found"));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{slug}")
	public ResponseEntity<Void> deleteTag(@PathVariable String slug) {

		tagService.deleteTag(slug);
		return ResponseEntity
				.noContent()
				.build();
	}
}
