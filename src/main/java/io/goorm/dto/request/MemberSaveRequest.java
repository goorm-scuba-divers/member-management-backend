package io.goorm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MemberSaveRequest(
        @NotNull(message = "username null이면 안 됨")
        @Schema(description = "아이디", example = "goorm")
        String username,

        @NotNull
        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @NotNull
        @Schema(description = "비밀번호", example = "1234")
        String password) {
}
