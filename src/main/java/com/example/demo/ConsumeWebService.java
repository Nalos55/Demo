package com.example.demo;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

public class ConsumeWebService {

	PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

	WebClient client = WebClient.builder()
			.baseUrl("http://localhost:8080")
			.defaultHeaders(headers -> headers.setBasicAuth("user1",
					encoder.encode("password")))
			.build();

	private Authentication getUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication;
	}

	@GetMapping("/client/users")
	public String getUsers(Model model) {
		JsonNode result = client.get()
				.uri("/users").accept(MediaType.APPLICATION_JSON)
				.retrieve().bodyToMono(JsonNode.class)
				.block(Duration.ofSeconds(1));

		List<User> users;

		if (!result.has("_embedded")) {
			users = new ArrayList<>();
			model.addAttribute("users", users);
			return "users";
		}

		result = result.get("_embedded").get("userList");
		result.forEach(e -> {
			ObjectNode objectNode = (ObjectNode) e;
			objectNode.remove("_links");
		});

		ObjectMapper mapper = new ObjectMapper();
		ObjectReader reader = mapper.readerFor(new TypeReference<List<User>>() {
		});

		try {
			users = reader.readValue(result);
			model.addAttribute("users", users);
			return "users";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@GetMapping("/client/users/registration")
	public String showRegistration(Model model) {
		User user = new User();
		model.addAttribute("user", user);
		return "registration";
	}

	@PostMapping("/client/users/registration")
	public String newUser(@ModelAttribute("user") User user, BindingResult bindingResult) {
		if (user.getUsername().length() <= 0) {
			bindingResult.rejectValue("name",
					"error.name", "Name must not be empty!");
			return "redirect:/client/users";
		}

		client.post()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/client/users";
	}

	@PostMapping("/client/users/delete/{username}")
	public String deleteUser(@PathVariable("username") String username) {

		client.delete()
				.uri("/users/" + username)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/client/users";
	}

	@GetMapping("/client/users/modify/{username}")
	public String showModify(Model model, @PathVariable("username") String username) {
		User user = client.get()
				.uri("/users/" + username)
				.retrieve()
				.bodyToMono(User.class)
				.block();
		model.addAttribute("user", user);
		return "put";
	}

	@PostMapping("/client/users/modify")
	public String modifyUser(@ModelAttribute("user") User user,
			BindingResult bindingResult) {
		client.put()
				.uri("/users")
				.bodyValue(user)
				.retrieve()
				.bodyToMono(String.class)
				.block();
		return "redirect:/client/users";
	}
}
