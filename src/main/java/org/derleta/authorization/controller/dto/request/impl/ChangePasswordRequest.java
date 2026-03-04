package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.derleta.authorization.controller.dto.request.Request;

public record ChangePasswordRequest(
        @NotNull Long userId,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 64) String actualPassword,
        @NotBlank @Size(min = 8, max = 64) String newPassword
) implements Request { }
