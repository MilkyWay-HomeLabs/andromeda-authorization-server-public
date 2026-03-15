package org.derleta.authorization.controller.dto.request;

public record UserRegistrationRequest(String username, String password, String email) implements Request {

}
