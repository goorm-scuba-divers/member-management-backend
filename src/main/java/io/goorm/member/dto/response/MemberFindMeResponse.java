package io.goorm.member.dto.response;

import io.goorm.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberFindMeResponse (
        @Schema(description = "닉네임", example = "scuba")
        String nickname
) {

    public static MemberFindMeResponse from(Member member) {
        return new MemberFindMeResponse(member.getNickname());
    }
}
