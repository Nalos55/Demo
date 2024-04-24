package com.example.demo.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Model.User;

public interface UserRepository extends JpaRepository<User, String> {
	@Override
	List<User> findAll();

	@Override
	void deleteById(String username);

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
}
