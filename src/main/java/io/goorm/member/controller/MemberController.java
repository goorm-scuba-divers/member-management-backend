package io.goorm.member.controller;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.request.MemberUpdateRequest;
import io.goorm.member.dto.response.MemberFindMeResponse;
import io.goorm.member.dto.response.MemberResponse;
import io.goorm.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Tag(name = "회원 API", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;

    // 리스트 조회
    @GetMapping()
    @Operation(summary = "회원 리스트 조회", description = "회원 리스트 조회 API")
    public Page<MemberResponse> findAll(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String searchValue,
            @RequestParam(required = false) MemberRole role
            ) {
        return memberService.findAll(pageable, searchValue, role);
    }

    // 내 프로필 조회
    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회", description = "내 프로필 조회 API")
    public MemberFindMeResponse findMember(@AuthenticationPrincipal PrincipalDetails userDetails) {
        return memberService.findMember(userDetails);
    }

    // 수정
    @PutMapping
    @Operation(summary = "내 프로필 수정", description = "내 프로필 수정 API")
    public void updateMember(@AuthenticationPrincipal PrincipalDetails userDetails,
                             @Valid @RequestBody MemberUpdateRequest request) {
        memberService.updateMember(userDetails, request);
    }

    // 삭제
    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴 API")
    public void deleteMember(@AuthenticationPrincipal PrincipalDetails userDetails) {
        memberService.deleteMember(userDetails);
    }
}
