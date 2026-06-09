package com.blogverse.api.service.impl;

import com.blogverse.api.domain.entity.Tag;
import com.blogverse.api.dto.response.TagResponse;
import com.blogverse.api.exception.ResourceNotFoundException;
import com.blogverse.api.repository.TagRepository;
import com.blogverse.api.service.TagService;
import com.blogverse.api.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {

	private final TagRepository tagRepository;

	@Override
	@Transactional
	public Tag findOrCreateTag(String name) {

		String slug = SlugUtils.toSlug(name);
		Optional<Tag> tag = tagRepository.findBySlug(slug);
		return tag.orElseGet(() -> tagRepository.save(Tag.builder()
			.name(name)
			.slug(slug)
			.build()));
	}

	@Override
	public List<TagResponse> findAllTags() {

		List<Tag> tags = tagRepository.findAll();
		return tags
			.stream()
			.map(this::maptoTagResponse)
			.toList();
	}

	@Override
	public TagResponse findBySlug(String slug) {

		Tag tag = tagRepository.findBySlug(slug)
			.orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

		return maptoTagResponse(tag);
	}

	@Override
	@Transactional
	public void deleteTag(String slug) {

		tagRepository.delete(tagRepository.findBySlug(slug)
			.orElseThrow(() -> new ResourceNotFoundException("Tag not found")));
	}

	private TagResponse maptoTagResponse(Tag tag) {

		return new TagResponse(
			tag.getId(),
			tag.getName(),
			tag.getSlug()
		);
	}
}
