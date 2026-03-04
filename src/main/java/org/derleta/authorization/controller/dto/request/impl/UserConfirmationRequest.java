package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.derleta.authorization.controller.dto.request.Request;

public record UserConfirmationRequest(
        @NotNull Long tokenId,
        @NotBlank @Size(min = 10, max = 512) String token
) implements Request { }
