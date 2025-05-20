package io.goorm.config.security;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.config.dto.TokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JWT 토큰을 요청 헤더에서 가져오기
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        System.out.println("token = " + token);

        // JWT 토큰 검증 및 사용자 정보 설정
        if (hasBearerToken(token)) {
            TokenDto tokenDto = jwtUtil.parseToken(getToken(token));

//            SecurityContextHolder.getContext().getAuthentication() == null
            if (tokenDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = new PrincipalDetails(tokenDto.memberId(), tokenDto.memberRole());
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(String token) {
        return token.substring(7);
    }

    private boolean hasBearerToken(String token) {
        return token != null && token.startsWith("Bearer ");
    }
}
