package io.goorm.service;

import io.goorm.dao.MemberRepository;
import io.goorm.domain.Member;
import io.goorm.dto.request.MemberSaveRequest;
import io.goorm.dto.response.MemberResponse;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 회원가입
    public void save(MemberSaveRequest request) {
        memberRepository.save(new Member(request.username(), request.nickname(), request.password()));
    }

    // 내 프로필 조회
    public void findMember() {
        Optional<Member> member = memberRepository.findById(1L);

        if (member.isEmpty()) {
            throw new IllegalStateException("member not found");
        }

        Member member1 = member.get();
    }

    // 전체 회원 조회
    public List<MemberResponse> findAll() {
        List<Member> members = memberRepository.findAll();
       return members.stream().map(MemberResponse::from).toList();

//        List<MemberResponse> responses = new ArrayList<>();
//        for (Member member : members) {
//            MemberResponse memberResponse =  MemberResponse.of(member);
//            responses.add(memberResponse);
//        }
//
//        return responses;
    }

    // 내 정보 수정
    public void changeMember() {}

    // 탈퇴
    public void deleteMember() {}
}
