package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.Model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Override
	List<Comment> findAll();

	@Override
	Optional<Comment> findById(Long id);

	@Query(value = "SELECT * "
			+ "FROM Comment "
			+ "WHERE parent IS NULL", nativeQuery = true)
	List<Comment> findAllRoot();

	@Query(value = "SELECT * "
			+ "FROM Comment "
			+ "WHERE parent IS NULL "
			+ "AND post = :postId", nativeQuery = true)
	List<Comment> findAllRootOfPost(@Param("postId") Long postId);

	@Query(value = "SELECT * "
			+ "FROM Comment "
			+ "WHERE parent = :parentId", nativeQuery = true)
	List<Comment> findAllChildrenOfComment(@Param("parentId") Long parentId);

	@Query(value = "SELECT * "
			+ "FROM Comment c1, Comment c2 "
			+ "WHERE c1.id = :commentId "
			+ "OR c1.id = c2.parent", nativeQuery = true)
	List<Comment> findCommentAndChildren(@Param("commentId") Long commentId);
}
