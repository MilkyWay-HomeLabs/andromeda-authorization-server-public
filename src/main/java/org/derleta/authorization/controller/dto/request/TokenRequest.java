package org.derleta.authorization.controller.dto.request;

public class TokenRequest implements Request {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
