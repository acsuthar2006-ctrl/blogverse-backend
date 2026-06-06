package com.blogverse.api.repository;

import com.blogverse.api.domain.entity.Author;
import com.blogverse.api.domain.entity.Post;
import com.blogverse.api.domain.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
	Optional<Post> findBySlug(String slug);

	Page<Post> findByAuthor(Author author, Pageable pageable);

	Page<Post> findByStatus(PostStatus status, Pageable pageable);
}
