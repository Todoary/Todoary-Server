package com.todoary.ms.src.service;


import com.todoary.ms.src.domain.Category;
import com.todoary.ms.src.repository.MemberRepository;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialColor;
import static com.todoary.ms.src.domain.Category.InitialCategoryValue.initialTitle;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void 멤버_회원가입시_기본_카테고리_생성() {
        // given
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        // when
        Long joinMemberId = memberService.join(memberJoinParam);
        // then
        List<Category> categories = memberService.findById(joinMemberId).getCategories();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getTitle()).isEqualTo(initialTitle);
        assertThat(categories.get(0).getColor()).isEqualTo(initialColor);
        assertThat(categories.get(0).getMember().getId()).isEqualTo(joinMemberId);
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
