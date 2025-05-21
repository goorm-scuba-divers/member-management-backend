package io.goorm.auth.controller;

import io.goorm.auth.dto.request.AuthSignInRequest;
import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.auth.service.AuthService;
import io.goorm.member.dto.request.MemberSaveRequest;
import io.goorm.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;

    public AuthController(MemberService memberService, AuthService authService) {
        this.memberService = memberService;
        this.authService = authService;
    }

    @PostMapping("/auth/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public void signUp(@Valid @RequestBody MemberSaveRequest request) {
        memberService.save(request);
    }

    @PostMapping("/auth/sign-in")
    @Operation(summary = "로그인", description = "로그인 API")
    public AuthSignInResponse signIn(@Valid @RequestBody AuthSignInRequest request) {
        return authService.signIn(request.username(), request.password());
    }
}
