package com.blogverse.api.controller;

import com.blogverse.api.dto.request.CategoryRequest;
import com.blogverse.api.dto.response.ApiResponse;
import com.blogverse.api.dto.response.CategoryResponse;
import com.blogverse.api.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> postCategory(
		@RequestBody @Valid CategoryRequest categoryRequest
	) {

		CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success(categoryResponse, "Category created successfully"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

		List<CategoryResponse> categoryResponseList = categoryService.findAllCategories();
		return ResponseEntity
			.ok(ApiResponse.success(categoryResponseList, "Categories found successfully"));
	}

	@GetMapping("/{slug}")
	public ResponseEntity<ApiResponse<CategoryResponse>> getCategory(@PathVariable String slug) {

		CategoryResponse categoryResponse = categoryService.findCategoryBySlug(slug);
		return ResponseEntity
			.ok(ApiResponse.success(categoryResponse, "Category found successfully"));
	}

	@PutMapping("/{slug}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
		@PathVariable String slug,
		@RequestBody @Valid CategoryRequest categoryRequest
	) {
		CategoryResponse categoryResponse = categoryService.updateCategoryBySlug(slug, categoryRequest);
		return ResponseEntity
			.ok(ApiResponse.success(categoryResponse, "Category updated successfully"));
	}

	@DeleteMapping("/{slug}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> deleteCategory(@PathVariable String slug) {
		categoryService.deleteCategoryBySlug(slug);
		return ResponseEntity
			.noContent()
			.build();
	}

}

