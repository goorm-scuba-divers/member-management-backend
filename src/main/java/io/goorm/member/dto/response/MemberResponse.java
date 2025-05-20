package io.goorm.member.dto.response;

import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record MemberResponse (
        @Schema(description = "고유 아이디")
        Long id,

        @Schema(description = "아이디", example = "goorm")
        String username,

        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "권한")
        MemberRole role,

        @Schema(description = "생성 일자")
        LocalDateTime createdAt,

        @Schema(description = "수정 일자")
        LocalDateTime modifiedAt
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getUsername(), member.getNickname(), member.getRole(), member.getCreatedAt(), member.getModifiedAt());
    }
}
