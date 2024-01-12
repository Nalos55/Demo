package com.example.demo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class PostModelAssembler implements RepresentationModelAssembler<Post, EntityModel<Post>> {

	@Override
	public EntityModel<Post> toModel(Post post) {
		return EntityModel.of(post,
				linkTo(methodOn(PostController.class).one(post.getOwner(), post.getId())).withSelfRel(),
				linkTo(methodOn(PostController.class).allOwned(post.getOwner())).withRel("from_same_owner"),
				linkTo(methodOn(PostController.class).all()).withRel("all"));
	}
}
