package io.goorm.config.security;

import io.goorm.auth.dao.RefreshTokenRepository;
import io.goorm.auth.domain.RefreshToken;
import io.goorm.auth.dto.response.AuthMemberResponse;
import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.auth.dto.response.AuthTokenResponse;
import io.goorm.config.dto.TokenDto;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtUtil {

    private final RefreshTokenRepository refreshTokenRepository;
    private final String secretKey;

    public JwtUtil(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${jwt.secretKey}") String secretKey
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.secretKey = secretKey;
    }

    public String generateAccessToken(Long memberId, MemberRole memberRole) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + 1000 * 60 * 60);

        return generateToken(memberId, memberRole, issuedAt, expiredAt);
    }

    public String generateRefreshToken(Long memberId, MemberRole memberRole) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + 1000L * 60 * 60 * 24 * 30);

        return generateToken(memberId, memberRole, issuedAt, expiredAt);
    }

    public Date getExpiredAtByToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getEncodedKey()).build().parseClaimsJws(token);
            return claimsJws.getBody().getExpiration();
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (Exception e) {
            return null;
        }
    }

    private String generateToken(Long memberId, MemberRole memberRole, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .setSubject(memberId.toString())
                .claim("role", memberRole.name())
                .setIssuedAt(issuedAt)
                .setExpiration(expiredAt)
                .signWith(getEncodedKey())
                .compact();
    }

    private SecretKey getEncodedKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public TokenDto parseToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(getEncodedKey()).build().parseClaimsJws(token);
            Long memberId = Long.parseLong(claimsJws.getBody().getSubject());
            MemberRole role = MemberRole.valueOf(claimsJws.getBody().get("role", String.class));
            return new TokenDto(memberId, role);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 리프레쉬 토큰 재발급
     * @param refreshToken
     * @return
     */
    public AuthTokenResponse refresh(String refreshToken) {
        TokenDto token = parseToken(refreshToken);
        if (token == null) throw new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED);

        // 만료되지 않은 리프레쉬 토큰을 DB에서 찾기
        RefreshToken findRefreshToken = refreshTokenRepository
                .findByValueAndExpiredAtIsAfter(refreshToken, LocalDateTime.now())
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_TOKEN_EXPIRED));

        Member member = findRefreshToken.getMember();

        return regenerateTokens(member).tokens();
    }

    /**
     * 새로운 토큰으로 교체
     * 리프레쉬 토큰은 검증을 위해 DB에 저장
     * @param member
     * @return
     */
    public AuthSignInResponse regenerateTokens(Member member) {
        AuthTokenResponse tokens = generateTokens(member.getId(), member.getRole());
        Date expiredAt = getExpiredAtByToken(tokens.refreshToken());

        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByMember(member);

        if (refreshToken.isEmpty()) {
            refreshTokenRepository.save(RefreshToken.createRefreshToken(tokens.refreshToken(), member, expiredAt));
        } else {
            refreshToken.get().rotate(tokens.refreshToken(), expiredAt);
        }

        AuthMemberResponse memberInfo = AuthMemberResponse.of(member.getId(), member.getUsername(), member.getNickname(), member.getRole());

        return AuthSignInResponse.of(tokens, memberInfo);
    }

    /**
     * 새로운 토큰 생성
     * @param memberId
     * @param role
     * @return
     */
    public AuthTokenResponse generateTokens(Long memberId, MemberRole role) {
        String accessToken = generateAccessToken(memberId, role);
        String refreshToken = generateRefreshToken(memberId, role);

        return AuthTokenResponse.of(accessToken, refreshToken);
    }
}
