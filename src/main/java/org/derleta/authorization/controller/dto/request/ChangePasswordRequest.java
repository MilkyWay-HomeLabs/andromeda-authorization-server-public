package org.derleta.authorization.controller.dto.request;

public record ChangePasswordRequest(long userId, String email, String actualPassword,
                                    String newPassword) implements Request {

}
