package io.goorm.member.service;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.request.MemberUpdateRequest;
import io.goorm.member.dto.response.MemberFindMeResponse;
import io.goorm.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 내 프로필 조회
    public MemberFindMeResponse findMember(PrincipalDetails userDetails) {
        return MemberFindMeResponse.from(getCurrentMember(userDetails));
    }

    // 전체 회원 조회
    public Page<MemberResponse> findAll(Pageable pageable, String searchValue, MemberRole role) {
        return memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)
                .map(MemberResponse::from);
    }

    // 내 정보 수정
    public void updateMember(PrincipalDetails userDetails, MemberUpdateRequest request) {
        Member currentMember = getCurrentMember(userDetails);

        if (!request.nickname().equals(currentMember.getNickname())) {
            currentMember.updateNickname(request.nickname());
        }

        if (request.currentPassword() != null) {
            boolean matches = passwordEncoder.matches(request.currentPassword(), currentMember.getPassword());
            boolean isSameAsNewPassword = passwordEncoder.matches(request.newPassword(), currentMember.getPassword());

            if (matches && isSameAsNewPassword) {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_SAME_AS_PREVIOUS);
            }

            if (matches) {
                currentMember.updatePassword(passwordEncoder.encode(request.newPassword()));
            } else {
                throw new CustomException(ErrorCode.MEMBER_PASSWORD_INVALID);
            }
        }
    }

    // 탈퇴
    public void deleteMember(PrincipalDetails userDetails) {
        Member member = getCurrentMember(userDetails);

        member.deleteMember();
    }

    private Member getCurrentMember(PrincipalDetails userDetails) {
        Long memberId = userDetails == null ? getCurrentMemberId() : Long.parseLong(userDetails.getUsername());

        return memberRepository.findById(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.AUTH_UNAUTHORIZED)
        );
    }

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (principal instanceof PrincipalDetails) {
            return Long.parseLong(((PrincipalDetails) principal).getUsername());
        }

        throw new CustomException(ErrorCode.AUTH_UNAUTHORIZED);
    }
}
