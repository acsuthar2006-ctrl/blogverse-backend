package com.blogverse.api.service;

import com.blogverse.api.domain.entity.Tag;
import com.blogverse.api.dto.response.TagResponse;

import java.util.List;

public interface TagService {
	Tag findOrCreateTag(String name);

	List<TagResponse> findAllTags();

	TagResponse findBySlug(String slug);

	void deleteTag(String slug);
}
