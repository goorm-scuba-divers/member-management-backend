package io.goorm.member.service;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.dto.request.MemberSaveRequest;
import io.goorm.member.dto.response.MemberFindMeResponse;
import io.goorm.member.dto.response.MemberResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 회원가입
    @Transactional
    public void save(MemberSaveRequest request) {
        String encodedPassword = passwordEncoder.encode(request.password());

        memberRepository.save(
                new Member(request.username(), request.nickname(), encodedPassword)
        );
    }

    // 내 프로필 조회
    public MemberFindMeResponse findMember() {
        return MemberFindMeResponse.from(getCurrentMember());
    }

    private Member getCurrentMember() {
        return memberRepository.findById(getCurrentMemberId()).orElseThrow(
                () -> new IllegalStateException("member not found")
        );
    }

    // 전체 회원 조회
    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();
        return members.stream().map(MemberResponse::from).toList();

//        List<MemberResponse> responses = new ArrayList<>();
//        for (Member member : members) {
//            MemberResponse memberResponse = MemberResponse.of(member);
//            responses.add(memberResponse);
//        }
//
//        return responses;
    }

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();
        if (principal instanceof PrincipalDetails) {
            return Long.parseLong(((PrincipalDetails) principal).getUsername());
        }

        throw new IllegalStateException("member unauthorized");
    }

    // 내 정보 수정
    public void changeMember() {}

    // 탈퇴
    public void deleteMember() {}
}
