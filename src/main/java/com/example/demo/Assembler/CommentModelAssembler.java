package com.example.demo.Assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.demo.Controller.CommentController;
import com.example.demo.Model.Comment;

@Component
public class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<Comment>> {

	@Override
	public EntityModel<Comment> toModel(Comment comment) {
		return EntityModel.of(comment,
				linkTo(methodOn(CommentController.class).one(comment.getId()))
						.withSelfRel());
	}
}
