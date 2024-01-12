package com.example.demo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	private final UserRepository repository;
	private final UserModelAssembler assembler;

	private static final Logger log = Logger.getLogger(UserController.class.getName());

	@Autowired
	private PasswordEncoder encoder;

	public UserController(UserRepository repository, UserModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}

	@GetMapping("/users")
	public CollectionModel<EntityModel<User>> all() {
		List<EntityModel<User>> users = repository.findAll().stream().map((user) -> {
			return assembler.toModel(user);
		})
				.collect(Collectors.toList());

		return CollectionModel.of(users,
				linkTo(methodOn(UserController.class).all()).withSelfRel());
	}

	@GetMapping("/users/{username}")
	public EntityModel<User> one(@PathVariable("username") String username) {
		User user = this.repository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(username));

		return assembler.toModel(user);
	}

	@PostMapping("/users")
	public ResponseEntity<?> newUser(@RequestBody User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		User newUser = repository.save(user);
		EntityModel<User> entityModel = assembler.toModel(newUser);
		return ResponseEntity
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entityModel);
	}

	@DeleteMapping("/users/{username}")
	public ResponseEntity<?> deleteUser(@PathVariable String username) {
		if (username == null) {
			return ResponseEntity.notFound().build();
		}
		if (repository.findByUsername(username).isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		repository.deleteById(username);
		return ResponseEntity.ok("Delete successful");
	}

	@PutMapping("/users")
	public ResponseEntity<?> putUser(@RequestBody User user) {
		user.setPassword(encoder.encode(user.getPassword()));
		if (user.getUsername() == null) {
			return ResponseEntity.notFound().build();
		}
//		if (repository.findById(user.getId()).isEmpty()) {
//			return ResponseEntity.badRequest().build();
//		}
		User savedUser = repository.findById(user.getUsername())
				.map(u -> {
					// log.info("map");
					u.setUsername(user.getUsername());
					return repository.save(u);
				})
				.orElseGet(() -> {
					// log.info("orElseGet");
					return repository.save(user);
				});

		EntityModel<User> entityModel = assembler.toModel(savedUser);
		return ResponseEntity
				.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entityModel);
	}
}
