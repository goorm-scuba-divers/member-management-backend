package io.goorm.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MemberSaveRequest(
        @NotNull(message = "username null이면 안 됨")
        @Schema(description = "아이디", example = "goorm")
        String username,

        @NotNull
        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @NotBlank
        @Size(min = 8, max = 20)
        @Schema(description = "비밀번호", example = "12345678")
        String password
) {
}
