package io.goorm.member.dao;

import io.goorm.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

    Page<Member> findAllByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.username = :username AND m.deletedAt IS NULL")
    Optional<Member> findByMember(String username);

    boolean existsByUsername(String username);

}
