package io.goorm.controller;

import io.goorm.dto.request.MemberSaveRequest;
import io.goorm.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping()
    public void save(@Valid @RequestBody MemberSaveRequest request) {
        memberService.save(request);
    }
}
