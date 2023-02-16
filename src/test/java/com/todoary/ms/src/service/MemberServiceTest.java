package com.todoary.ms.src.service;


import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.MemberJoinParam;
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
        MemberJoinParam memberJoinParam = createMemberJoinParam();

        //when
        Long joinMemberId = memberService.join(memberJoinParam);

        //then
        assertThat(memberService.findById(joinMemberId).getName()).isEqualTo(memberJoinParam.getName());
    }

    MemberJoinParam createMemberJoinParam() {
        return new MemberJoinParam("memberA",
                "nicknameA",
                "emailA",
                "passwordA",
                "ROLE_USER",
                true);
    }
}
