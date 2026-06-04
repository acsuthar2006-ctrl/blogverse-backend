package com.blogverse.api.domain.entity;

import com.blogverse.api.domain.enums.PostStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(unique = true, nullable = false)
	private String slug;

	@Column(nullable = false , columnDefinition = "TEXT")
	private String content;

	@ManyToOne
	@JoinColumn(nullable = false , name = "author_id")
	private Author author;

	@Builder.Default
	@Enumerated(EnumType.STRING)
	private PostStatus status = PostStatus.DRAFT;

	@Column(length = 500)
	private String summary;

	private LocalDateTime publishedAt;

	@CreationTimestamp
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
