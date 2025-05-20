package io.goorm.member.controller;

import io.goorm.member.dto.request.MemberSaveRequest;
import io.goorm.member.dto.response.MemberFindMeResponse;
import io.goorm.member.service.MemberService;
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

    // 리스트 조회
//    @GetMapping()

    // 단수 조회
//    @GetMapping("/{id}")

    // 내 프로필 조회
    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "내 프로필 조회 API")
    public MemberFindMeResponse findMember() {
        return memberService.findMember();
    }

    // 수정
//    @PutMapping

    // 삭제
//    @DeleteMapping()
}
