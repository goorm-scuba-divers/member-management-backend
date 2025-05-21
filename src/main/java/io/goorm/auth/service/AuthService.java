package io.goorm.auth.service;

import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.config.security.JwtUtil;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

     private final MemberRepository memberRepository;
     private final JwtUtil jwtUtil;
     private final PasswordEncoder passwordEncoder;

     public AuthService(MemberRepository memberRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
         this.memberRepository = memberRepository;
         this.jwtUtil = jwtUtil;
         this.passwordEncoder = passwordEncoder;
     }

     @Transactional
     public AuthSignInResponse signIn(String username, String password) {
         Member member = memberRepository.findByUsername(username);
         if (member == null || !passwordEncoder.matches(password, member.getPassword())) {
             throw new BadCredentialsException("Invalid username or password");
         }

         String accessToken = jwtUtil.generateAccessToken(member.getId(), member.getRole());
         String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getRole());

         return AuthSignInResponse.of(accessToken, refreshToken);
     }
}
