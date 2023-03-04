package com.todoary.ms.src.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.todoary.ms.src.service.MemberServiceTest.createMemberJoinParamOfEmail;
import static com.todoary.ms.src.service.MemberServiceTest.createOauthMemberJoinParamOfEmail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ExpiredMemberHandlerServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    ExpiredMemberHandlerService expiredMemberHandlerService;
    @MockBean
    DateTimeProvider dateTimeProvider;
    @SpyBean
    AuditingHandler handler;

    @Test
    void 탈퇴한지_30일지난_일반_멤버_삭제됨() {
        // given
        handler.setDateTimeProvider(dateTimeProvider);

        Long memberId = memberService.joinGeneralMember(createMemberJoinParamOfEmail("email@email.com"));

        given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2023, 2, 2, 0, 0)));
        memberService.deactivateMember(memberId);

        // when
        int deletedMembers = expiredMemberHandlerService.deleteMembersDeactivatedTimeBefore(LocalDateTime.of(2023, 3, 4, 0, 1).minusDays(30));
        // then
        assertThat(deletedMembers).isOne();
    }

    @Test
    void 탈퇴한지_30일지난_애플_멤버_삭제됨() {
        // given
        handler.setDateTimeProvider(dateTimeProvider);

        Long memberId = memberService.joinOauthMember(createOauthMemberJoinParamOfEmail("email@email.com"));

        given(dateTimeProvider.getNow()).willReturn(Optional.of(LocalDateTime.of(2023, 2, 2, 0, 0)));
        memberService.deactivateMember(memberId);

        // when
        int deletedMembers = expiredMemberHandlerService.deleteMembersDeactivatedTimeBefore(LocalDateTime.of(2023, 3, 4, 0, 1).minusDays(30));
        // then
        assertThat(deletedMembers).isOne();
    }
}