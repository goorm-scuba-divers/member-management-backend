package io.goorm.auth.dto.response;

import io.goorm.member.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthSignInResponse (
        @Schema(description = "액세스 토큰")
        String accessToken,

        @Schema(description = "리프레시 토큰")
        String refreshToken,

        @Schema(description = "고유 아이디")
        Long id,

        @Schema(description = "아이디", example = "goorm")
        String username,

        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "권한")
        MemberRole role
) {
    public static AuthSignInResponse of(String accessToken, String refreshToken, Long id, String username, String nickname, MemberRole role) {
        return new AuthSignInResponse(accessToken, refreshToken, id, username, nickname, role);
    }
}
