package io.goorm.controller;

import io.goorm.dto.request.MemberSaveRequest;
import io.goorm.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@Tag(name = "회원 API", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping()
    @Operation(summary = "회원가입", description = "회원가입 API")
    public void save(@Valid @RequestBody MemberSaveRequest request) {
        memberService.save(request);
    }
}
