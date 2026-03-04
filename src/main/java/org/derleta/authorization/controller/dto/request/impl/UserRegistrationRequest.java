package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.derleta.authorization.controller.dto.request.Request;

public record UserRegistrationRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Email @Size(max = 254) String email
) implements Request { }
