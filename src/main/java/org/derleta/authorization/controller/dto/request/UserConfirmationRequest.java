package org.derleta.authorization.controller.dto.request;

public record UserConfirmationRequest(Long tokenId, String token) implements Request {

}
