
package com.example.demo;

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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
public class PostConsumer {

	WebClient client = WebClient.builder()
			.baseUrl("http://localhost:8080/api/posts")
			.build();

	@GetMapping("/posts")
	public String showPosts2(Model model) {
		JsonNode result = client.get()
				.uri("/all")
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(JsonNode.class)
				.block();
		List<Post> posts = new ArrayList<>();

		if (result.has("_embedded")) {
			result = result.get("_embedded").get("postList");
			result.forEach(e -> {
				ObjectNode objectNode = (ObjectNode) e;
				objectNode.remove("_links");
			});

			ObjectMapper mapper = new ObjectMapper();
			ObjectReader reader = mapper.readerFor(new TypeReference<List<Post>>() {
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
				.uri("/new")
				.bodyValue(post)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/posts";
	}

	@PostMapping("/deletePost/{owner}/{id}")
	public String deletePost(@PathVariable("owner") String owner, @PathVariable("id") Long id) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) auth.getPrincipal();

		if (!owner.equals(currentUser.getUsername())) {
			return "redirect:/posts";
		}

		client.delete()
				.uri("/delete/" + id)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/posts";
	}
}
