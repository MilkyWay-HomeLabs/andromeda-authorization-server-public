package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.derleta.authorization.controller.dto.request.Request;

public record ResetPasswordRequest(
        @NotBlank @Email @Size(max = 254) String email
) implements Request { }
