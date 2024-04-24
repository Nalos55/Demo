package com.example.demo.Model;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String content;

	@ManyToOne
	@JoinColumn(name = "post")
	private Post post;

	@ManyToOne
	@JoinColumn(name = "parent", nullable = true)
	private Comment parent;

	@OneToMany(mappedBy = "parent")
	@JsonIgnore
	private List<Comment> children;

	public Comment() {
	};

	public Comment(Comment parent, String content) {
		setParent(parent);
		setContent(content);
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Post getPost() {
		return post;
	}

	public void setId(Long commentId) {
		id = commentId;
	}

	public Long getId() {
		return id;
	}

//	public Comment getParent() {
//		return parent;
//	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Comment> getChildren() {
		return this.children;
	}

	public Optional<Comment> getParent() {
		if (parent == null)
			return Optional.empty();
		return Optional.of(parent);
	}
}
