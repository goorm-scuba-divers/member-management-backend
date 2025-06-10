package io.goorm.member.service;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.config.exception.CustomException;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.domain.MemberRole;
import io.goorm.member.dto.request.MemberUpdateRequest;
import io.goorm.member.dto.response.MemberFindMeResponse;
import io.goorm.member.dto.response.MemberResponse;
import io.goorm.member.dto.response.PageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    private PrincipalDetails principalDetails;

    @BeforeEach
    void setUp() {
        member = new Member("test", "testNickname", "testPassword");

        ReflectionTestUtils.setField(member, "id", 1L);

        principalDetails = new PrincipalDetails(member.getId(), member.getRole());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities())
        );
    }

    @Test
    void 내_프로필_조회() {

        //given
        long memberId = Long.parseLong(principalDetails.getUsername());

        //when
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        MemberFindMeResponse response = memberService.findMember(principalDetails);

        //then
        Assertions.assertEquals(response.nickname(), member.getNickname());
        Assertions.assertEquals(response.role(), member.getRole());
    }

    @Test
    void 내_프로필_조회_실패_존재하지_않는_아이디() {

        //given
        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(CustomException.class, () -> memberService.findMember(principalDetails));
    }

    @Test
    void 전체_회원_조회() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = "test";
        MemberRole role = MemberRole.USER;
            List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(1, response.content().size());
        Assertions.assertEquals(member.getNickname(), response.content().get(0).nickname());
        Assertions.assertEquals(member.getUsername(), response.content().get(0).username());
    }

    @Test
    void 전체_회원_조회_searchValue가_없는_경우() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = null;
        MemberRole role = MemberRole.USER;

        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(1, response.content().size());
    }

    @Test
    void 전체_회원_조회_searchValue의_결과가_존재하지_않는_경우() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = "XXXX";
        MemberRole role = MemberRole.USER;

        List<Member> memberList = List.of();
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, 0);

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(0, response.content().size());
    }

    @Test
    void 전체_회원_조회_role이_없는_경우() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = "test";
        MemberRole role = null;

        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(1, response.content().size());
    }

    @Test
    void 전체_회원_조회_searchValue와_role이_없는_경우() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = null;
        MemberRole role = null;

        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(1, response.content().size());
    }

    @Test
    void 전체_회원_조회_role이_ADMIN인_경우() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String searchValue = "test";
        MemberRole role = MemberRole.ADMIN;

        ReflectionTestUtils.setField(member, "role", MemberRole.ADMIN);

        List<Member> memberList = List.of(member);
        Page<Member> memberPage = new PageImpl<>(memberList, pageable, memberList.size());

        // when
        when(memberRepository.findAllByPageableAndFilter(pageable, searchValue, role)).thenReturn(memberPage);
        PageResponse<MemberResponse> response = memberService.findAll(pageable, searchValue, role);

        // then
        Assertions.assertEquals(1, response.content().size());
    }

    @Test
    void 내_정보_수정_닉네임만() {

        //given
        String newNickname = "newNickname";
        MemberUpdateRequest request = new MemberUpdateRequest(newNickname, null, null);

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        memberService.updateMember(principalDetails, request);

        //then
        Assertions.assertEquals(newNickname, member.getNickname());
    }

    @Test
    void 내_정보_수정_패스워드만() {

        //given
        String currentPassword = "testPassword";
        String newPassword = "newTestPassword";
        MemberUpdateRequest request = new MemberUpdateRequest(null, currentPassword, newPassword);

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(currentPassword, member.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

//        System.out.println(member);
        memberService.updateMember(principalDetails, request);

        //then
        Assertions.assertEquals("encodedNewPassword", member.getPassword());
    }

    @Test
    void 내_정보_수정_닉네임_패스워드_모두() {

        //given
        String newNickname = "newNickname";
        String currentPassword = "testPassword";
        String newPassword = "newTestPassword";
        MemberUpdateRequest request = new MemberUpdateRequest(newNickname, currentPassword, newPassword);

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(currentPassword, member.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        memberService.updateMember(principalDetails, request);

        //then
        Assertions.assertEquals(newNickname, member.getNickname());
        Assertions.assertEquals("encodedNewPassword", member.getPassword());
    }

    @Test
    void 내_정보_수정_실패_현재_비밀번호가_틀린_경우() {

        //given
        String newNickname = "newNickname";
        String currentPassword = "wrongPassword";
        String newPassword = "newTestPassword";
        MemberUpdateRequest request = new MemberUpdateRequest(newNickname, currentPassword, newPassword);

        //when
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(currentPassword, member.getPassword())).thenReturn(false);

        //then
        Assertions.assertThrows(CustomException.class, () -> memberService.updateMember(principalDetails, request));
    }

    @Test
    void 탈퇴() {

        //given
        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));

        //when
        memberService.deleteMember(principalDetails);

        LocalDateTime deletedAt = member.getDeletedAt();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(deletedAt, now);

        //then
        Assertions.assertNotNull(member.getDeletedAt());
        Assertions.assertTrue(duration.getSeconds() < 1);
    }
}
