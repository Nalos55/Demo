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

import com.example.demo.Assembler.CommentModelAssembler;
import com.example.demo.Model.Comment;
import com.example.demo.Service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	private CommentService service;
	private CommentModelAssembler assembler;

	CommentController(CommentService service, CommentModelAssembler assembler) {
		this.service = service;
		this.assembler = assembler;
	}

	@GetMapping("/{id}")
	public EntityModel<Comment> one(@PathVariable("id") Long id) {
		return assembler.toModel(service.findById(id));
	}

	@GetMapping("/all")
	public CollectionModel<EntityModel<Comment>> all() {
		return buildCollection(service.getAllComments());
	}

	@GetMapping("/allRoot")
	public CollectionModel<EntityModel<Comment>> allRoot() {
		return buildCollection(service.getAllRootComments());
	}

	@GetMapping("/allRoot/{postId}")
	public CollectionModel<EntityModel<Comment>> allRootOfPost(@PathVariable("postId") Long postId) {
		return buildCollection(service.getAllRootCommentsOfPost(postId));
	}

	@GetMapping("/allChildren/{commentId}")
	public CollectionModel<EntityModel<Comment>> allChildrenOFComment(@PathVariable("commentId") Long commentId) {
		return buildCollection(service.getAllChildrenOfComment(commentId));
	}

	@PostMapping("/new/{postId}/{parentId}")
	public ResponseEntity<?> newComment(@PathVariable("postId") Long postId, @PathVariable("parentId") Long parentId,
			@RequestBody Comment comment) {
		return buildCreatedResponse(service.createChildComment(comment, postId, parentId));
	}

	@PostMapping("/new/{postId}")
	public ResponseEntity<?> newRootComment(@PathVariable("postId") Long postId,
			@RequestBody Comment comment) {
		return buildCreatedResponse(service.createComment(comment, postId));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
		if (service.deleteComment(id)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	private ResponseEntity<?> buildCreatedResponse(Comment comment) {
		if (comment == null) {
			return ResponseEntity.badRequest().build();
		}
		EntityModel<Comment> entity = assembler.toModel(comment);
		return ResponseEntity
				.created(entity.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(entity);
	}

	private CollectionModel<EntityModel<Comment>> buildCollection(List<Comment> comments) {
		List<EntityModel<Comment>> entities = comments.stream().map(assembler::toModel)
				.collect(Collectors.toList());
		return CollectionModel.of(entities,
				linkTo(methodOn(CommentController.class).all()).withSelfRel());
	}
}
