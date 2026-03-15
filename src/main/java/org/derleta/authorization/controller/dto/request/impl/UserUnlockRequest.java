package org.derleta.authorization.controller.dto.request.impl;

import jakarta.validation.constraints.NotNull;
import org.derleta.authorization.controller.dto.request.Request;

public record UserUnlockRequest(
        @NotNull Long userId
) implements Request { }
