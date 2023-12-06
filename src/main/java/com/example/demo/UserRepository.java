package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
	@Override
	List<User> findAll();

	void deleteByUsername(String username);

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
}
