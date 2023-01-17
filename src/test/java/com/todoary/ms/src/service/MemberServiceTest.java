package com.todoary.ms.src.service;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void 멤버_회원가입() throws Exception {
        //given
        Member member = createMember();

        //when
        Long joinMemberId = memberService.join(member);

        //then
        assertThat(joinMemberId).isEqualTo(member.getId());
    }

    Member createMember() {
        Member member = Member.builder().build();
        return member;
    }
}
