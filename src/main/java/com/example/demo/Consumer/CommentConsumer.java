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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

@Controller
public class CommentConsumer {
	WebClient client = WebClient.builder()
			.baseUrl("http://localhost:8080/api/comments")
			.build();

	@GetMapping("/comments")
	public String showComments(Model model) {
		JsonNode result = client.get()
				.uri("/all")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		List<Comment> comments = decodeJson(result);

		model.addAttribute("comments", comments);
		return "comments";
	}

	@GetMapping("/rootComments")
	public String showRootComments(Model model) {
		JsonNode result = client.get()
				.uri("/allRoot")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		List<Comment> comments = new ArrayList<>();

		comments = decodeJson(result);

		model.addAttribute("comments", comments);
		return "comments";
	}

//	/{postId}
//	model.addAttribute("postId", postId);
	@GetMapping("/newComment/{postId}")
	public String showAddPost(Model model, @PathVariable("postId") Long postId) {
		Comment comment = new Comment();
		model.addAttribute("comment", comment);
		model.addAttribute("postId", postId);
		return "newComment";
	}

	@PostMapping("/comments/{postId}")
	public String addRootComment(@ModelAttribute("comment") Comment comment, @PathVariable("postId") Long postId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User owner = (User) auth.getPrincipal();
//		comment.setOwner(owner.getUsername());
		client.post()
				.uri("/new/" + postId)
				.bodyValue(comment)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/comments";
	}

	@PostMapping("/comments/{postId}/{parentId}")
	public String addComment(@ModelAttribute("comment") Comment comment, @PathVariable("postId") Long postId,
			@PathVariable("parentId") Long parentId) {
//		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//		User owner = (User) auth.getPrincipal();
//		comment.setOwner(owner.getUsername());

		client.post()
				.uri("/new/" + postId + "/" + parentId)
				.bodyValue(comment)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/comments";
	}

//	@ResponseBody
	@GetMapping("/allRoot/{postId}")
	public String retrieveRootComments(@PathVariable("postId") Long postId, Model model) {
		JsonNode result = client.get()
				.uri("/allRoot/" + postId)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		List<Comment> comments = decodeJson(result);
		model.addAttribute("comments", comments);
		return "comments";
//		return result.toString();
	}

	@GetMapping("/comment/{id}")
	public String getComment(@PathVariable("id") Long id, Model model) {
		JsonNode result = client.get()
				.uri("/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		Comment mainComment = null;
		ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<Comment>() {
		});
		try {
			mainComment = reader.readValue(result);
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute("mainComment", mainComment);

		result = client.get()
				.uri("/allChildren/" + id)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();

		List<Comment> children = decodeJson(result);

		model.addAttribute("children", children);
		return "comment";
	}

	private List<Comment> decodeJson(JsonNode json) {
		List<Comment> comments = new ArrayList<>();
		if (json.has("_embedded")) {
			json = json.get("_embedded").get("commentList");

			ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<List<Comment>>() {
			});

			try {
				comments = reader.readValue(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return comments;
	}
}
