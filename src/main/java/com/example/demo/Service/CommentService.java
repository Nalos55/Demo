package com.example.demo.Service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.Assembler.CommentModelAssembler;
import com.example.demo.Exception.CommentNotFoundException;
import com.example.demo.Model.Comment;
import com.example.demo.Model.Post;
import com.example.demo.Repository.CommentRepository;

@Service
public class CommentService {

	private CommentRepository repository;
	private PostService postService;

	CommentService(CommentRepository repository, CommentModelAssembler assembler, PostService postService) {
		this.repository = repository;
		this.postService = postService;
	}

	public Comment findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new CommentNotFoundException(id));
	}

	public Comment updateComment(Long id, Comment comment) {
		Comment oldComment = findById(id);
		Post post = oldComment.getPost();
		postService.removeComment(post, oldComment);
		repository.deleteById(id);
		comment.setId(id);
		postService.addComment(post, oldComment);
		return repository.save(comment);
	}

	public Comment createChildComment(Comment comment, Long postId, Long parentId) {
		if (parentId == null)
			return null;
		Post post = postService.findById(postId);
		Comment parent = findById(parentId);

		parent.getChildren().add(comment);
		comment.setPost(post);
		comment.setParent(parent);

		parent = repository.save(parent);
		comment = repository.save(comment);
		postService.addComment(post, comment);

		return comment;
	}

	public Comment createComment(Comment comment, Long postId) {
		Post post = postService.findById(postId);
		comment.setPost(post);
		comment = repository.save(comment);

		postService.addComment(post, comment);
		return comment;
	}

	public boolean deleteComment(Long id) {
		if (repository.findById(id).isEmpty()) {
			return false;
		}
		Comment comment = findById(id);
		Post post = comment.getPost();
		repository.deleteById(id);
		postService.removeComment(post, comment);
		return true;
	}

	public List<Comment> getAllComments() {
		return repository.findAll();
	}

	public List<Comment> getAllRootComments() {
		return repository.findAllRoot();
	}

	public List<Comment> getAllRootCommentsOfPost(Long postId) {
		return repository.findAllRootOfPost(postId);
	}

	public List<Comment> getAllChildrenOfComment(Long commentId) {
		return repository.findAllChildrenOfComment(commentId);
	}
}
