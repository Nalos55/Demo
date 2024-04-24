
package com.example.demo.Consumer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.Model.Comment;
import com.example.demo.Model.Post;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Controller
public class PostConsumer {

	WebClient client = WebClient.builder()
			.baseUrl("http://localhost:8080/api")
			.build();

	@GetMapping("/posts")
	public String showPosts(Model model) {
		JsonNode result = client.get()
				.uri("/posts/all")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		List<Post> posts = new ArrayList<>();

		if (result.has("_embedded")) {
			result = result.get("_embedded").get("postList");
//			result.forEach(e -> {
//				ObjectNode objectNode = (ObjectNode) e;
//				objectNode.remove("_links");
//			});

			ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<Post>>() {
			});

			try {
				posts = reader.readValue(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("posts", posts);
		return "posts";
	}

//	@ResponseBody
	@GetMapping("/post/{id}")
	public String showPost(@PathVariable("id") Long id, Model model) {
		JsonNode result = client.get()
				.uri("/posts/id/" + id)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		if (result.isEmpty())
			return null;

		ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<Post>() {
		});

		Post post = null;
		try {
			post = reader.readValue(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute("post", post);

		result = client.get()
				.uri("/comments/allRoot/" + id)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		if (result.isEmpty())
			return null;

		List<Comment> rootComments = new ArrayList<>();
		if (result.has("_embedded")) {
			result = result.get("_embedded").get("commentList");

			reader = new ObjectMapper().readerFor(new TypeReference<List<Comment>>() {
			});

			try {
				rootComments = reader.readValue(result);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		model.addAttribute("rootComments", rootComments);

		return "post";
//		return result.toString();
	}

	@GetMapping("/newPost")
	public String showAddPost(Model model) {
		Post post = new Post();
		model.addAttribute("post", post);
		return "newPost";
	}

	@PostMapping("/posts")
	public String addPost(@ModelAttribute("post") Post post) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User owner = (User) auth.getPrincipal();
		post.setOwner(owner.getUsername());
		client.post()
				.uri("/posts/new")
				.bodyValue(post)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/posts";
	}

	@PostMapping("/deletePost/{id}")
	public String deletePost(@PathVariable("id") Long id) {
		client.delete()
				.uri("/posts/delete/" + id)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/posts";
	}

	/*
	 * @PostMapping("/{postId}/newComment") public String
	 * addComment(@PathVariable("postId") Long postId) { Authentication auth =
	 * SecurityContextHolder.getContext().getAuthentication(); User currentUser =
	 * (User) auth.getPrincipal();
	 * 
	 * Comment comment = new Comment(null, currentUser.getUsername(), content);
	 * comment.setPostId(postId); }
	 * 
	 */
}
