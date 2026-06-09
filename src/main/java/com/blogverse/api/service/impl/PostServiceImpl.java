package com.blogverse.api.service.impl;

import com.blogverse.api.domain.entity.Author;
import com.blogverse.api.domain.entity.Category;
import com.blogverse.api.domain.entity.Post;
import com.blogverse.api.domain.entity.Tag;
import com.blogverse.api.domain.enums.PostStatus;
import com.blogverse.api.dto.request.CreatePostRequest;
import com.blogverse.api.dto.request.UpdatePostRequest;
import com.blogverse.api.dto.response.AuthorSummary;
import com.blogverse.api.dto.response.CategorySummary;
import com.blogverse.api.dto.response.PostResponse;
import com.blogverse.api.dto.response.PostSummaryResponse;
import com.blogverse.api.exception.ResourceNotFoundException;
import com.blogverse.api.repository.AuthorRepository;
import com.blogverse.api.repository.CategoryRepository;
import com.blogverse.api.repository.PostRepository;
import com.blogverse.api.service.PostService;
import com.blogverse.api.service.TagService;
import com.blogverse.api.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final AuthorRepository authorRepository;

	private final TagService tagService;

	@Override
	@Transactional
	public PostResponse createPost(CreatePostRequest createPostRequest, Author author) {
		String slug = SlugUtils.toUniqueSlug(
				createPostRequest.title(),
				s -> postRepository.findBySlug(s).isEmpty()
		);

		Set<Category> categories = createPostRequest.categories()
			.stream()
			.map(c -> categoryRepository.findBySlug(c).orElseThrow(
				() -> new ResourceNotFoundException("Category not found")))
			.collect(Collectors.toSet());

		Set<Tag> tags = createPostRequest.tags()
			.stream()
			.map(tagService::findOrCreateTag)
			.collect(Collectors.toSet());

		Post post = Post.builder()
			.slug(slug)
			.title(createPostRequest.title())
			.content(createPostRequest.content())
			.summary(createPostRequest.summary())
			.author(author)
			.categories(categories)
			.tags(tags)
			.build();

		Post savedPost = postRepository.save(post);

		return maptoPostResponse(savedPost);
	}

	@Override
	public PostResponse getPostBySlug(String slug) {
		Post post = postRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found for slug: " + slug));

		return maptoPostResponse(post);
	}

	@Override
	public Page<PostSummaryResponse> findAllPostsByAuthor(String username, Pageable pageable) {

		Author author = authorRepository.findByUserName(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for username: " + username));

		Page<Post> posts = postRepository.findByAuthor(author, pageable);
		return posts.map(this::maptoPostSummaryResponse);
	}

	@Override
	public Page<PostSummaryResponse> findAllPublishedPosts(Pageable pageable) {
		Page<Post> posts = postRepository.findByStatus(PostStatus.PUBLISHED, pageable);
		return posts.map(this::maptoPostSummaryResponse);
	}

	@Override
	@Transactional
	public PostResponse updatePost(UpdatePostRequest updatePostRequest, String slug, Author author) {
		Post post = postRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found for slug: " + slug));

		if (!post.getAuthor().getId().equals(author.getId())) {
			throw new AccessDeniedException("You are not authorized to modify this post");
		}

		if (updatePostRequest.title() != null) {
			post.setTitle(updatePostRequest.title());
		}
		if (updatePostRequest.content() != null) {
			post.setContent(updatePostRequest.content());
		}
		if (updatePostRequest.summary() != null) {
			post.setSummary(updatePostRequest.summary());
		}
		if (updatePostRequest.status() != null) {
			post.setStatus(updatePostRequest.status());
			if (updatePostRequest.status() == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
				post.setPublishedAt(LocalDateTime.now());
			}
		}
		if (updatePostRequest.categories() != null) {
			Set<Category> newCategories = updatePostRequest.categories()
				.stream()
				.map(c -> categoryRepository.findBySlug(c).orElseThrow(
					() -> new ResourceNotFoundException("Category not found for slug: " + c)
				))
				.collect(Collectors.toSet());

			post.getCategories().clear();
			post.getCategories().addAll(newCategories);
		}
		if (updatePostRequest.tags() != null) {
			Set<Tag> newTags = updatePostRequest.tags()
				.stream()
				.map(tagService::findOrCreateTag)
				.collect(Collectors.toSet());

			post.getTags().clear();
			post.getTags().addAll(newTags);
		}

		return maptoPostResponse(postRepository.save(post));
	}

	@Override
	@Transactional
	public void deletePost(String slug, Author author) {
		Post post = postRepository.findBySlug(slug)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found for slug: " + slug));

		if (!post.getAuthor().getId().equals(author.getId())) {
			throw new AccessDeniedException("You are not authorized to delete this post");
		}

		postRepository.delete(post);
	}

	private PostResponse maptoPostResponse(Post post) {

		return new PostResponse(
			post.getId(),
			post.getTitle(),
			post.getContent(),
			post.getSlug(),
			post.getSummary(),
			post.getStatus(),
			new AuthorSummary(post.getAuthor().getFullName(), post.getAuthor().getUsername()),
			post.getCreatedAt(),
			post.getPublishedAt() ,
			post.getCategories().stream()
				.map(this::mapToCategorySummary)
				.collect(Collectors.toList()) ,
			post.getTags().stream()
				.map(Tag::getName)
				.collect(Collectors.toList())
		);
	}

	private PostSummaryResponse maptoPostSummaryResponse(Post post) {

		return new PostSummaryResponse(
			post.getId(),
			post.getTitle(),
			post.getSlug(),
			post.getSummary(),
			post.getStatus(),
			new AuthorSummary(post.getAuthor().getFullName(), post.getAuthor().getUsername()),
			post.getCreatedAt(),
			post.getPublishedAt());
	}

	private CategorySummary mapToCategorySummary(Category category) {
		return new CategorySummary(category.getName(), category.getSlug());
	}

}
