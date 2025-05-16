package io.goorm.dto.request;

import jakarta.validation.constraints.NotNull;

public record MemberSaveRequest(
        @NotNull(message = "username null이면 안됌")
        String username,
        @NotNull
        String nickname,
        @NotNull
        String password) {
}
