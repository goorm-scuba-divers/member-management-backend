package io.goorm.member.dto.response;

import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberFindMeResponse (
        @Schema(description = "닉네임", example = "scuba")
        String nickname,

        @Schema(description = "권한")
        MemberRole role
) {

    public static MemberFindMeResponse from(Member member) {
        return new MemberFindMeResponse(member.getNickname(), member.getRole());
    }
}
