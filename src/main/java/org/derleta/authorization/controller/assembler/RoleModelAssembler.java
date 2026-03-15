package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.RoleController;
import org.derleta.authorization.controller.dto.response.RoleResponse;
import org.derleta.authorization.domain.model.Role;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

/**
 * The RoleModelAssembler is a Spring component that extends the
 * {@link RepresentationModelAssemblerSupport} to convert {@link Role} entities
 * into {@link RoleResponse} HATEOAS-compliant models.
 * This assembler streamlines the process of mapping entities into models and
 * adding HATEOAS links to enhance the representation.
 */
@Component
public class RoleModelAssembler extends RepresentationModelAssemblerSupport<Role, RoleResponse> {

    public RoleModelAssembler() {
        super(RoleController.class, RoleResponse.class);
    }

    /**
     * Converts a {@link Role} entity into a {@link RoleResponse} model, copying all relevant
     * properties and adding a self-referential HATEOAS link to the model.
     *
     * @param entity the {@link Role} entity to be converted into a {@link RoleResponse} model
     * @return the {@link RoleResponse} model populated with copied properties and an added self-referential link
     */
    @Override
    public RoleResponse toModel(Role entity) {
        RoleResponse model = new RoleResponse();
        model.setRoleId(entity.roleId());
        model.setRoleName(entity.roleName());
        Link selfLink = linkTo(RoleController.class).slash(RoleController.DEFAULT_PATH).slash(model.getRoleId()).withSelfRel();
        model.add(selfLink);
        return model;
    }


}
