package io.goorm.auth.service;

import io.goorm.auth.dao.RefreshTokenRepository;
import io.goorm.auth.domain.RefreshToken;
import io.goorm.auth.dto.response.AuthTokenResponse;
import io.goorm.config.dto.TokenDto;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.config.security.JwtUtil;
import io.goorm.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * 리프레쉬 토큰 재발급
     * @param refreshToken
     * @return
     */
    @Transactional
    public AuthTokenResponse refresh(String refreshToken) {
        TokenDto token = jwtUtil.parseToken(refreshToken);
        if (token == null) throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);

        // 만료되지 않은 리프레쉬 토큰을 DB에서 찾기
        RefreshToken findRefreshToken = refreshTokenRepository
                .findByValueAndExpiredAtIsAfter(refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED));

        Member member = findRefreshToken.getMember();

        return regenerateAndStoreTokens(member);
    }

    public AuthTokenResponse regenerateAndStoreTokens(Member member) {
        // 토큰 생성
        AuthTokenResponse tokens = jwtUtil.generateTokens(member.getId(), member.getRole());

        // 만료 시간 추출
        Date expiredAt = jwtUtil.getExpiredAtByToken(tokens.refreshToken());

        // 리프레시 토큰 저장 or 갱신
        refreshTokenRepository.findByMember(member)
                .ifPresentOrElse(
                        token -> token.rotate(tokens.refreshToken(), expiredAt),
                        () -> refreshTokenRepository.save(RefreshToken.createRefreshToken(tokens.refreshToken(), member, expiredAt))
                );

        return tokens;
    }

}
