package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.AccessTokenController;
import org.derleta.authorization.controller.dto.response.AccessTokenResponse;
import org.derleta.authorization.domain.model.AccessToken;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


/**
 * Assembler class responsible for converting {@link AccessToken} entities into
 * {@link AccessTokenResponse} models. This class also adds HATEOAS support by
 * attaching self-referential links to the generated models.
 */
@Component
public class AccessTokenModelAssembler extends RepresentationModelAssemblerSupport<AccessToken, AccessTokenResponse> {

    public AccessTokenModelAssembler() {
        super(AccessTokenController.class, AccessTokenResponse.class);
    }

    /**
     * Converts an {@code AccessToken} entity into an {@code AccessTokenResponse} model.
     * Copies all relevant properties from the {@code AccessToken} entity to the
     * {@code AccessTokenResponse} model, including the user information, and adds
     * a self-referential HATEOAS link to the model.
     *
     * @param item the {@code AccessToken} entity to be converted
     * @return the {@code AccessTokenResponse} model with copied properties and an added self-referential link
     */
    @Override
    public AccessTokenResponse toModel(AccessToken item) {
        AccessTokenResponse model = new AccessTokenResponse();
        model.setTokenId(item.tokenId());
        model.setUserId(item.userId());
        model.setExpirationDate(item.expirationDate());
        model.setVersion(item.version());
        model.setJti(item.jti());
        model.setRevoked(item.revoked());

        Link selfLink = linkTo(AccessTokenController.class)
                .slash(AccessTokenController.DEFAULT_PATH)
                .slash(model.getTokenId())
                .withSelfRel();
        model.add(selfLink);

        return model;
    }

}
