package com.example.demo.Exception;

public class PostNotFoundException extends RuntimeException {

	public PostNotFoundException(String owner, Long id) {
		super("Could not find post with owner: " + owner + " and id: " + id);
	}

	public PostNotFoundException(String owner) {
		super("Could not find posts with owner: " + owner);
	}

	public PostNotFoundException(Long id) {
		super("Could not find posts with id: " + id);
	}

	public PostNotFoundException() {
		super("Could not find posts");
	}
}
