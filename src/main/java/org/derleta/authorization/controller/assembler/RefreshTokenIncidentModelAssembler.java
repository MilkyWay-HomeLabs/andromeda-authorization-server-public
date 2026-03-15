package org.derleta.authorization.controller.assembler;

import org.derleta.authorization.controller.RefreshTokenController;
import org.derleta.authorization.controller.RefreshTokenIncidentController;
import org.derleta.authorization.controller.dto.response.RefreshTokenIncidentResponse;
import org.derleta.authorization.domain.entity.token.RefreshTokenIncidentEntity;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Component
public class RefreshTokenIncidentModelAssembler extends RepresentationModelAssemblerSupport<RefreshTokenIncidentEntity, RefreshTokenIncidentResponse> {

    public RefreshTokenIncidentModelAssembler() {
        super(RefreshTokenIncidentController.class, RefreshTokenIncidentResponse.class);
    }


    @Override
    public RefreshTokenIncidentResponse toModel(RefreshTokenIncidentEntity item) {
        RefreshTokenIncidentResponse model = new RefreshTokenIncidentResponse();
        model.setId(item.getId());
        model.setTokenId(item.getTokenId());
        model.setUserId(item.getUserId());
        model.setJti(item.getJti());
        model.setVersion(item.getVersion());
        model.setIncidentTime(item.getIncidentTime());
        model.setIpAddress(item.getIpAddress());
        model.setUserAgent(item.getUserAgent());
        model.setDescription(item.getDescription());
        Link selfLink = linkTo(RefreshTokenIncidentController.class).slash(RefreshTokenController.DEFAULT_PATH).slash(model.getTokenId()).withSelfRel();
        model.add(selfLink);
        return model;
    }

}
