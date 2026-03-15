package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.UserController;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.domain.model.User;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * The UserModelAssembler is a Spring component that extends the
 * {@link RepresentationModelAssemblerSupport} to convert {@link User} entities
 * into {@link UserResponse} HATEOAS-compliant models.
 * This assembler simplifies the process of adding HATEOAS links to the output models.
 */
@Component
public class UserModelAssembler extends RepresentationModelAssemblerSupport<User, UserResponse> {

    public UserModelAssembler() {
        super(UserController.class, UserResponse.class);
    }

    /**
     * Converts a {@link User} entity into a {@link UserResponse} model, copying
     * all relevant properties and adding a self-referential HATEOAS link to the model.
     *
     * @param entity the {@link User} entity to be converted into a {@link UserResponse} model
     * @return the {@link UserResponse} model with copied properties and an added self-referential link
     */
    @Override
    public UserResponse toModel(User entity) {
        UserResponse model = new UserResponse();
        model.setUserId(entity.userId());
        model.setUsername(entity.username());
        model.setEmail(entity.email());
        Link selfLink = linkTo(UserController.class).slash(UserController.DEFAULT_PATH).slash(model.getUserId()).withSelfRel();
        model.add(selfLink);
        return model;
    }

}
