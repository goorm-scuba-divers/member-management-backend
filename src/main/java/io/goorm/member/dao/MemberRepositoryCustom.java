package io.goorm.member.dao;

import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findAllByPageableAndFilter(Pageable pageable, String searchValue, MemberRole role);
}
