package com.example.demo.Exception;

public class CommentNotFoundException extends RuntimeException {

	public CommentNotFoundException(Long id) {
		super("Could not find comment with id " + id);
	}
}
