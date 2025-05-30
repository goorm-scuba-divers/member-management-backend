package io.goorm.member.dao;

import io.goorm.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> findAllByPageableAndFilter(Pageable pageable, String username);
}
