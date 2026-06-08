package com.blogverse.api.service.impl;

import com.blogverse.api.domain.entity.Category;
import com.blogverse.api.dto.request.CategoryRequest;
import com.blogverse.api.dto.response.CategoryResponse;
import com.blogverse.api.exception.ResourceNotFoundException;
import com.blogverse.api.repository.CategoryRepository;
import com.blogverse.api.service.CategoryService;
import com.blogverse.api.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	@Transactional
	public CategoryResponse createCategory(CategoryRequest categoryRequest) {

		if (categoryRepository.findByName(categoryRequest.name()).isPresent()) {
			throw new IllegalArgumentException("Category already exists");
		}

		Category category =  Category.builder()
			.name(categoryRequest.name())
			.slug(SlugUtils.toSlug(categoryRequest.name()))
			.description(categoryRequest.description())
			.build();

		return mapToCategoryResponse(categoryRepository.save(category));
	}

	@Override
	public List<CategoryResponse> findAllCategories() {

		List<Category> categories = categoryRepository.findAll();

		return categories
			.stream()
			.map(this::mapToCategoryResponse)
			.toList();
	}

	@Override
	public CategoryResponse findCategoryBySlug(String slug) {

		Category category = categoryRepository.findBySlug(slug)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		return mapToCategoryResponse(category);
	}

	@Override
	@Transactional
	public CategoryResponse updateCategoryBySlug(String slug, CategoryRequest categoryRequest) {

		Category category = categoryRepository.findBySlug(slug)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"));

		if (categoryRequest.name() != null && !categoryRequest.name().equals(category.getName())) {

			if (categoryRepository.findByName(categoryRequest.name()).isPresent()) {
				throw new IllegalArgumentException("Category name already exists");
			}

			category.setName(categoryRequest.name());
			String newSlug = SlugUtils.toSlug(categoryRequest.name());
			category.setSlug(newSlug);
		}

		if (categoryRequest.description() != null) {
			category.setDescription(categoryRequest.description());
		}

		return mapToCategoryResponse(categoryRepository.save(category));
	}

	@Override
	@Transactional
	public void deleteCategoryBySlug(String slug) {
		categoryRepository.delete(categoryRepository.findBySlug(slug)
			.orElseThrow(() -> new ResourceNotFoundException("Category not found"))
		);
	}

	private CategoryResponse mapToCategoryResponse(Category category) {

		return new  CategoryResponse(
			category.getId(),
			category.getName(),
			category.getSlug(),
			category.getDescription()
		);
	}

}
