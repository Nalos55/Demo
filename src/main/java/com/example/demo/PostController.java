package com.example.demo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private PostRepository repository;
	private PostModelAssembler assembler;

	PostController(PostRepository repository, PostModelAssembler assembler) {
		this.repository = repository;
		this.assembler = assembler;
	}

	@GetMapping("/{owner}/{id}")
	public EntityModel<Post> one(@PathVariable("owner") String owner, @PathVariable("id") Long id) {
		Post post = repository.findByOwnerAndId(owner, id)
				.orElseThrow(() -> new PostNotFoundException(owner, id));
		return assembler.toModel(post);
	}

	@GetMapping("/{owner}")
	public CollectionModel<EntityModel<Post>> allOwned(@PathVariable("owner") String owner) {
		List<Post> posts = repository.findByOwner(owner);
		if (posts.isEmpty())
			throw new PostNotFoundException(owner);
		List<EntityModel<Post>> entities = posts.stream().map(assembler::toModel).collect(Collectors.toList());
		return CollectionModel.of(entities,
				linkTo(methodOn(PostController.class).allOwned(owner)).withSelfRel());
	}

	@GetMapping("/all")
	public CollectionModel<EntityModel<Post>> all() {
		List<EntityModel<Post>> entities = repository.findAll().stream().map(assembler::toModel)
				.collect(Collectors.toList());
		return CollectionModel.of(entities,
				linkTo(methodOn(PostController.class).all()).withSelfRel());
	}

	@PostMapping("/new")
	public ResponseEntity<?> newPost(@RequestBody Post post) {
		Post newPost = repository.save(new Post(post.getOwner(), post.getTitle(), post.getContent()));
		EntityModel<Post> entity = assembler.toModel(newPost);
		return ResponseEntity
				.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entity);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
		if (repository.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		System.out.println(repository.findById(id).get());
		repository.deleteById(id);
		return ResponseEntity.ok("Delete successful");
	}
}
