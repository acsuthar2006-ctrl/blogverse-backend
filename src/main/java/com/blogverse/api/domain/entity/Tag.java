package com.blogverse.api.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tags")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column( nullable = false, length = 100 , unique = true )
	private String name;

	@Column( nullable = false, length = 100 , unique = true )
	private String slug;
}
