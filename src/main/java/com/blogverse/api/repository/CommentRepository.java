package com.blogverse.api.repository;

import com.blogverse.api.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPostIdAndParentCommentIsNull(Long postId);

	List<Comment> findByParentCommentId(Long parentCommentId);

}
