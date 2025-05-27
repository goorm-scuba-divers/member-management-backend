package io.goorm.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthSignInRequest(
        @NotNull(message = "로그인 ID를 입력해주세요.")
        @Schema(description = "아이디", example = "goorm")
        String username,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        @Schema(description = "비밀번호", example = "12345678")
        String password
) {
}
