package io.goorm.dto.response;

import io.goorm.domain.Member;
import io.goorm.domain.MemberRole;

import java.time.LocalDateTime;

public class MemberResponse {
    private final Long id;
    private final String username;
    private final String nickname;
    private final MemberRole role;
    private final LocalDateTime createdAt;

    private MemberResponse(Long id, String username, String nickname, MemberRole role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getUsername(), member.getNickname(), member.getRole(), member.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public MemberRole getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
