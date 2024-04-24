package com.example.demo.Controller;

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

import com.example.demo.Assembler.PostModelAssembler;
import com.example.demo.Model.Post;
import com.example.demo.Service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private PostService service;
	private PostModelAssembler assembler;

	PostController(PostService service, PostModelAssembler assembler) {
		this.service = service;
		this.assembler = assembler;
	}

	@GetMapping("/id/{id}")
	public EntityModel<Post> one(@PathVariable("id") Long id) {
		return assembler.toModel(service.findById(id));
	}

//	@GetMapping("/{owner}")
//	public CollectionModel<EntityModel<Post>> allOwned(@PathVariable("owner") String owner) {
//		List<Post> posts = repository.findByOwner(owner);
//		if (posts.isEmpty())
//			throw new PostNotFoundException(owner);
//		List<EntityModel<Post>> entities = posts.stream().map(assembler::toModel).collect(Collectors.toList());
//		return CollectionModel.of(entities,
//				linkTo(methodOn(PostController.class).allOwned(owner)).withSelfRel());
//	}

	@GetMapping("/all")
	public CollectionModel<EntityModel<Post>> all() {
		return buildCollection(service.getAllPosts());
	}

	@PostMapping("/new")
	public ResponseEntity<?> newPost(@RequestBody Post post) {
		return buildCreatedResponse(service.createPost(post));
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deletePost(@PathVariable("id") Long id) {
		if (service.deletePost(id)) {
			return ResponseEntity.ok("Delete successful");
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	private ResponseEntity<?> buildCreatedResponse(Post post) {
		if (post == null) {
			return ResponseEntity.badRequest().build();
		}
		EntityModel<Post> entity = assembler.toModel(post);
		return ResponseEntity
				.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entity);
	}

	private CollectionModel<EntityModel<Post>> buildCollection(List<Post> posts) {
		List<EntityModel<Post>> entities = posts.stream().map(assembler::toModel)
				.collect(Collectors.toList());
		return CollectionModel.of(entities,
				linkTo(methodOn(PostController.class).all()).withSelfRel());
	}
}
