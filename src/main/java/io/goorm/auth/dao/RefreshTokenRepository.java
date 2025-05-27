package io.goorm.auth.dao;

import io.goorm.auth.domain.RefreshToken;
import io.goorm.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByValueAndExpiredAtIsAfter(String refreshToken, LocalDateTime expiredAt);

    Optional<RefreshToken> findByMember(Member member);

}
