package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.NotBlank;
import org.derleta.authorization.controller.dto.request.Request;

public class TokenRequest implements Request {
    @NotBlank
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
