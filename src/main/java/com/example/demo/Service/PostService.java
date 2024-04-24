package com.example.demo.Service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.example.demo.Exception.PostNotFoundException;
import com.example.demo.Model.Comment;
import com.example.demo.Model.Post;
import com.example.demo.Repository.PostRepository;

@Service
public class PostService {

	private PostRepository repository;

	PostService(PostRepository repository) {
		this.repository = repository;
	}

	public Post findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new PostNotFoundException(id));
	}

	public Post createPost(Post post) {
		post = repository.save(post);
		return post;
	}

	public Post updatePost(Long id, Post post) {
		repository.deleteById(id);
		post.setId(id);
		return repository.save(post);
	}

	public boolean deletePost(Long id) {
		if (repository.findById(id).isEmpty()) {
			return false;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal();

		if (!findById(id).getOwner().equals(currentUser.getUsername())) {
			return false;
		}
		repository.deleteById(id);
		return true;
	}

	public List<Post> getAllPosts() {
		return repository.findAll();
	}

	public void addComment(Post post, Comment comment) {
		List<Comment> comments = post.getComments();
		comments.add(comment);
		repository.save(post);
	}

	public void removeComment(Post post, Comment comment) {
		List<Comment> comments = post.getComments();
		comments.remove(comment);
		repository.save(post);
	}
}
