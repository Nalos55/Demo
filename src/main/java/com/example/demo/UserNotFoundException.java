package com.example.demo;

public class UserNotFoundException extends RuntimeException {

	UserNotFoundException(String username) {
		super("Could not find user with username: " + username);
	}
}
