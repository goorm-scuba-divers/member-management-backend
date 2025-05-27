package io.goorm.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.util.StringUtils;

public record MemberUpdateRequest (
        @NotBlank
        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "현재 비밀번호", example = "12345678")
        String currentPassword,

        @Schema(description = "새 비밀번호", example = "12345678")
        String newPassword
) {
    @AssertTrue(message = "현재 비밀번호가 있을 경우, 새 비밀번호는 최소 8자 이상이어야 합니다.")
    public boolean isNewPasswordValid() {
        if (StringUtils.hasText(currentPassword)) {
            return StringUtils.hasText(newPassword) && newPassword.length() >= 8;
        }
        return true;
    }
}
