package io.goorm.config.security;

import io.goorm.config.dto.TokenDto;
import io.goorm.member.domain.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secretKey}")
    private String secretKey;


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

    public String generateToken(Long memberId, MemberRole memberRole, Date issuedAt, Date expiredAt) {
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
            throw e;
        } catch (Exception e) {
            return null;
        }

    }

}
