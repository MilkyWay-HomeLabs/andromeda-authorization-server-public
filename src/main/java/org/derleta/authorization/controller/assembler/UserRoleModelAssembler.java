package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.UserRoleController;
import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.controller.dto.response.UserResponse;
import org.derleta.authorization.controller.dto.response.UserRoleResponse;
import org.derleta.authorization.domain.model.UserRole;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * Assembles {@link UserRole} entities into their corresponding API response model,
 * {@link UserRoleResponse}, for representation in RESTful API responses.
 * Includes logic for adding HATEOAS self-referential links to the assembled models.
 */
@Component
public class UserRoleModelAssembler extends RepresentationModelAssemblerSupport<UserRole, UserRoleResponse> {

    private final UserModelAssembler userModelAssembler;
    private final RoleModelAssembler roleModelAssembler;

    public UserRoleModelAssembler(UserModelAssembler userModelAssembler, RoleModelAssembler roleModelAssembler) {
        super(UserRoleController.class, UserRoleResponse.class);
        this.userModelAssembler = userModelAssembler;
        this.roleModelAssembler = roleModelAssembler;
    }

    /**
     * Converts a {@link UserRole} entity into a {@link UserRoleResponse} model and adds a self-referential link
     * pointing to the resource location.
     *
     * @param entity the {@link UserRole} entity to be converted
     * @return a {@link UserRoleResponse} model containing the converted data and self-referential link
     */
    @Override
    public UserRoleResponse toModel(UserRole entity) {
        UserRoleResponse model = toUserRolesModel(entity);

        Link selfLink = linkTo(UserRoleController.class)
                .slash(UserRoleController.DEFAULT_PATH)
                .slash(model.getUserRoleId())
                .withSelfRel();
        model.add(selfLink);

        return model;
    }

    /**
     * Converts a {@link UserRole} entity into a {@link UserRoleResponse} model.
     *
     * @param entity the {@link UserRole} entity to be converted
     * @return a {@link UserRoleResponse} model containing the converted data
     */
    private UserRoleResponse toUserRolesModel(UserRole entity) {
        UserRoleResponse model = new UserRoleResponse();
        model.setUserRoleId(entity.userRoleId());

        UserResponse userResponse = userModelAssembler.toModel(entity.user());
        model.setUser(userResponse);

        RoleResponse roleResponse = roleModelAssembler.toModel(entity.role());
        model.setRole(roleResponse);

        return model;
    }

}
