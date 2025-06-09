package io.goorm.member.service;

import io.goorm.config.dto.PrincipalDetails;
import io.goorm.config.exception.CustomException;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.dto.response.MemberFindMeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
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
    void 내_프로필_조회_실패_없는_아이디() {
        //given
        //when

        when(memberRepository.findById(member.getId())).thenReturn(Optional.empty());

        //then
        Assertions.assertThrows(CustomException.class, () -> memberService.findMember(principalDetails));

    }

    @Test
    void 전체_회원_조회() {

    }

    @Test
    void 내_정보_수정_닉네임만() {

    }

    @Test
    void 내_정보_수정_패스워드만() {

    }

    @Test
    void 내_정보_수정_닉네임_패스워드() {

    }

    @Test
    void 탈퇴() {
        //delete 로직 수행

        //deletedAt != null && deletedAt == currentTime
        // findById -> member가 리턴이 되어야하고,
        // member.deleteMember(); 현재 날짜로 바꾸어야 하고;

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