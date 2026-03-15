package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.UserRoleController;
import org.derleta.authorization.controller.UserRolesController;
import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.controller.dto.response.UserRolesResponse;
import org.derleta.authorization.domain.model.UserRoles;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * A model assembler responsible for converting {@code UserRoles} entities into their respective
 * {@code UserRolesResponse} representation models, while adding HATEOAS links for resource navigation.
 * Provides methods for mapping data from the entity to the response model and generating self-links
 * using relevant API paths.
 */
@Component
public class UserRolesModelAssembler extends RepresentationModelAssemblerSupport<UserRoles, UserRolesResponse> {

    private final UserModelAssembler userModelAssembler;
    private final RoleModelAssembler roleModelAssembler;

    public UserRolesModelAssembler(UserModelAssembler userModelAssembler, RoleModelAssembler roleModelAssembler) {
        super(UserRoleController.class, UserRolesResponse.class);
        this.userModelAssembler = userModelAssembler;
        this.roleModelAssembler = roleModelAssembler;
    }

    /**
     * Converts a {@code UserRoles} entity into a {@code UserRolesResponse} model by mapping the entity's
     * data, adding a self-link pointing to the resource's API path, and returning the resulting model.
     *
     * @param entity the {@code UserRoles} entity to be converted
     * @return a {@code UserRolesResponse} model containing the entity's data and a self-link
     */
    @Override
    public UserRolesResponse toModel(UserRoles entity) {
        UserRolesResponse model = toUserRolesModel(entity);
        Link selfLink = linkTo(UserRolesController.class).slash(UserRolesController.DEFAULT_PATH).slash(model.getUser().getUserId()).withSelfRel();
        model.add(selfLink);
        return model;
    }

    /**
     * Converts a {@code UserRoles} entity into a {@code UserRolesResponse} model by copying properties
     * and mapping nested entities.
     *
     * @param entity the {@code UserRoles} entity to convert
     * @return a {@code UserRolesResponse} model containing the entity's data and additional nested mappings
     */
    private UserRolesResponse toUserRolesModel(UserRoles entity) {
        UserRolesResponse model = new UserRolesResponse();

        model.setUser(userModelAssembler.toModel(entity.user()));
        Set<RoleResponse> roles = new HashSet<>();
        for (var role : entity.roles()) {
            roles.add(roleModelAssembler.toModel(role));
        }
        model.setRoles(roles);

        return model;
    }

}
