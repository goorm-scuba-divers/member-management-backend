package io.goorm.dto.response;

import io.goorm.domain.Member;

public class MemberResponse {
    private Long id;
    private String username;
    private String nickname;

    private MemberResponse(Long id, String username, String nickname) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getUsername(), member.getNickname());
    }
}
