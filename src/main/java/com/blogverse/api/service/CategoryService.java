package com.blogverse.api.service;

import com.blogverse.api.dto.request.CategoryRequest;
import com.blogverse.api.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
	CategoryResponse createCategory(CategoryRequest categoryRequest);

	List<CategoryResponse> findAllCategories();

	CategoryResponse findCategoryBySlug(String slug);

	CategoryResponse updateCategoryBySlug(String slug , CategoryRequest categoryRequest);

	void deleteCategoryBySlug(String slug);
}
