package io.goorm.auth.service;

import io.goorm.auth.dao.RefreshTokenRepository;
import io.goorm.auth.domain.RefreshToken;
import io.goorm.auth.dto.response.AuthMemberInfo;
import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.auth.dto.response.AuthTokenInfo;
import io.goorm.config.dto.TokenDto;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.config.security.JwtUtil;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.request.MemberSaveRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class AuthService {

     private final MemberRepository memberRepository;
     private final RefreshTokenRepository refreshTokenRepository;
     private final JwtUtil jwtUtil;
     private final PasswordEncoder passwordEncoder;

     /**
      * 로그인
      * @param username
      * @param password
      * @return
      */
     public AuthSignInResponse signIn(String username, String password) {
         Member member = memberRepository.findByMember(username).orElseThrow(
                 () -> new CustomException(ErrorCode.AUTH_USERNAME_PASSWORD_INVALID)
         );
         if (!passwordEncoder.matches(password, member.getPassword()))
             throw new CustomException(ErrorCode.AUTH_USERNAME_PASSWORD_INVALID);

         return regenerateTokens(member);
     }

     /**
      * 회원가입
      * @param request
      * @return
      */
    public AuthSignInResponse save(MemberSaveRequest request) {
        if (memberRepository.existsByUsername(request.username()))
            throw new CustomException(ErrorCode.MEMBER_EXISTS);

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = new Member(request.username(), request.nickname(), encodedPassword);
        memberRepository.save(member);

        AuthMemberInfo memberInfo = AuthMemberInfo.from(member.getId(), member.getUsername(), member.getNickname(), member.getRole());

        return AuthSignInResponse.of(generateTokens(member.getId(), member.getRole()), memberInfo);
    }

    /**
     * 리프레쉬 토큰 재발급
     * @param refreshToken
     * @return
     */
    public AuthSignInResponse refresh(String refreshToken) {
        TokenDto token = jwtUtil.parseToken(refreshToken);
        if (token == null) throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);

        // 만료되지 않은 리프레쉬 토큰을 DB에서 찾기
        RefreshToken findRefreshToken = refreshTokenRepository
                .findByValueAndExpiredAtIsAfter(refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED));

        Member member = findRefreshToken.getMember();

        return regenerateTokens(member);
    }

    /**
     * 새로운 토큰으로 교체
     * 리프레쉬 토큰은 검증을 위해 DB에 저장
     * @param member
     * @return
     */
    private AuthSignInResponse regenerateTokens(Member member) {
        AuthTokenInfo tokens = generateTokens(member.getId(), member.getRole());
        Date expiredAt = jwtUtil.getExpiredAtByToken(tokens.refreshToken());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);

        if (refreshToken.isEmpty()) {
            refreshTokenRepository.save(RefreshToken.createRefreshToken(tokens.refreshToken(), member, expiredAt));
        } else {
            refreshToken.get().rotate(tokens.refreshToken(), expiredAt);
        }

        AuthMemberInfo memberInfo = AuthMemberInfo.from(member.getId(), member.getUsername(), member.getNickname(), member.getRole());

        return AuthSignInResponse.of(tokens, memberInfo);
    }

    /**
     * 새로운 토큰 생성
     * @param memberId
     * @param role
     * @return
     */
    private AuthTokenInfo generateTokens(Long memberId, MemberRole role) {
        String accessToken = jwtUtil.generateAccessToken(memberId, role);
        String refreshToken = jwtUtil.generateRefreshToken(memberId, role);

        return AuthTokenInfo.of(accessToken, refreshToken);
    }

    /**
     * 로그아웃
     * @param refreshToken
     */
    public void signOut(String refreshToken) {
        TokenDto token = jwtUtil.parseToken(refreshToken);
        if (token == null) throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);

        RefreshToken findRefreshToken = refreshTokenRepository
                .findByValueAndExpiredAtIsAfter(refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED));

        refreshTokenRepository.delete(findRefreshToken);
    }

}
