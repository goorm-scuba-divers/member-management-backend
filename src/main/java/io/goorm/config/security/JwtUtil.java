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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
