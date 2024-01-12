package com.example.demo;

public class PostNotFoundException extends RuntimeException {

	PostNotFoundException(String owner, Long id) {
		super("Could not find post with owner: " + owner + " and id: " + id);
	}

	PostNotFoundException(String owner) {
		super("Could not find posts with owner: " + owner);
	}

	PostNotFoundException() {
		super("Could not find posts");
	}
}
