package io.goorm.auth.dto.response;

import io.goorm.member.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record AuthMemberResponse(
        @Schema(description = "고유 아이디")
        Long id,

        @Schema(description = "아이디", example = "goorm")
        String username,

        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "권한")
        MemberRole role
) {
    public static AuthMemberResponse of(Long id, String username, String nickname, MemberRole role) {
        return new AuthMemberResponse(id, username, nickname, role);
    }
}
