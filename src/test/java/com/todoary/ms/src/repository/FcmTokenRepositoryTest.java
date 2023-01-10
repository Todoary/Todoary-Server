package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.RemindAlarm;
import com.todoary.ms.src.domain.token.FcmToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class FcmTokenRepositoryTest {

    @Autowired
    EntityManager em;
    @Autowired
    FcmTokenRepository fcmTokenRepository;

    @Test
    public void 리마인드_알람_활성화되고_타켓날짜와_같은_멤버의_fcmtoken_리스트_반환() throws Exception {
        //given
        LocalDate targetDate = LocalDate.of(2023, 01, 01);
        Member member1 = createMember();
        FcmToken fcmToken1 = new FcmToken(member1, "fcmToken1");
        RemindAlarm remindAlarm1 = new RemindAlarm(member1, targetDate);

        fcmTokenRepository.save(fcmToken1);
        em.persist(remindAlarm1);

        Member member2 = createMember();
        FcmToken fcmToken2 = new FcmToken(member2, "fcmToken2");
        RemindAlarm remindAlarm2 = new RemindAlarm(member2, targetDate);

        fcmTokenRepository.save(fcmToken2);
        em.persist(remindAlarm2);

        Member member3 = createMember();

        //when
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllByRemindDate(targetDate);

        //then
        assertThat(fcmTokens.size()).isEqualTo(2);
    }
    @Test
    public void 데일리_알람_활성화된_멤버의_fcmtoken_리스트_반환() throws Exception {
        //given
        Member member1 = createMember();
        FcmToken fcmToken1 = new FcmToken(member1, "fcmtoken1");
        fcmTokenRepository.save(fcmToken1);

        Member member2 = createMember();
        FcmToken fcmToken2 = new FcmToken(member2, "fcmtoken2");
        fcmTokenRepository.save(fcmToken2);

        Member member3 = createMember();

        //when
        List<FcmToken> fcmTokens = fcmTokenRepository.findAllByDailyEnabled();

        //then
        assertThat(fcmTokens.size()).isEqualTo(2);
    }

    Member createMember() {
        Member member = Member.builder()
                .name("member")
                .nickname("memberNickname")
                .email("memberEmail")
                .password("123456")
                .isTermsEnable(true)
                .build();

        em.persist(member);
        return member;
    }
}