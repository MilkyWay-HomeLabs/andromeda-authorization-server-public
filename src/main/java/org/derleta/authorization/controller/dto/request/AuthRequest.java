package org.derleta.authorization.controller.dto.request;

public interface AuthRequest extends Request {

    String getLogin();

    String getPassword();

}
