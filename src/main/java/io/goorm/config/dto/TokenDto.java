package io.goorm.config.dto;

import io.goorm.member.domain.MemberRole;

public record TokenDto(Long memberId, MemberRole memberRole) {

}
