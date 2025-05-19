package io.goorm.config.dto;

import io.goorm.domain.MemberRole;

public record TokenDto(Long memberId, MemberRole memberRole) {

}
