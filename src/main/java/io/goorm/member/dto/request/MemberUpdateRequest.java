package io.goorm.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import org.springframework.util.StringUtils;

public record MemberUpdateRequest(
        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "현재 비밀번호", example = "12345678")
        @Size(min = 8, message = "기존 비밀번호는 최소 8자 이상이어야 합니다.")
        String currentPassword,

        @Schema(description = "새 비밀번호", example = "12345678")
        @Size(min = 8, message = "새 비밀번호는 최소 8자 이상이어야 합니다.")
        String newPassword
) {
    @AssertTrue(message = "새 비밀번호는 현재 비밀번호와 달라야 합니다.")
    public boolean isPasswordChanged() {
        if (StringUtils.hasText(currentPassword) && StringUtils.hasText(newPassword))
            return !currentPassword.equals(newPassword);

        return true;
    }
}