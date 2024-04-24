package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Override
	List<Post> findAll();

	Optional<Post> findByOwnerAndId(String owner, Long id);

	List<Post> findByOwner(String owner);

	@Override
	Optional<Post> findById(Long id);

	@Override
	void deleteById(Long id);

//	@Modifying
//	@Query(nativeQuery = true, value = "UPDATE Post "
//			+ "SET comments = :comments "
//			+ "WHERE id = :id")
//	void updatePostCommentList(@Param("comments") List<Comment> comments, @Param("id") Long id);
}
