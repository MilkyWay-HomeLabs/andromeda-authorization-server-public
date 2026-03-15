package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.derleta.authorization.controller.dto.request.AuthRequest;
import org.hibernate.validator.constraints.Length;

public class AuthLoginRequest implements AuthRequest {

    @NotNull
    @Length(min = 5, max = 50)
    private String login;

    @NotBlank
    @Length(min = 5, max = 64)
    private String password;

    public AuthLoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public AuthLoginRequest() {
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
