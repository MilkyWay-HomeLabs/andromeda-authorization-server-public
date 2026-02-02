package org.derleta.authorization.controller.dto.request;

public record UserRequest(String username, String password, String email) implements Request {

}
