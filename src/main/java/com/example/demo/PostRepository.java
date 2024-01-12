package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Override
	List<Post> findAll();

	Optional<Post> findByOwnerAndId(String owner, Long id);

	List<Post> findByOwner(String owner);

	@Override
	Optional<Post> findById(Long id);

	@Override
	void deleteById(Long id);
}
