package com.todoary.ms.src.repository;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class RefreshTokenRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Test
    public void insertRefreshToken_유저_존재O() throws Exception {
        //given
        Member member = createMember();
        RefreshToken refreshToken = new RefreshToken(member, "refreshToken");

        refreshTokenRepository.save(refreshToken);

        RefreshToken findToken = em.createQuery("select r from RefreshToken r where r.member = :member", RefreshToken.class)
                .setParameter("member", member)
                .getSingleResult();
        //then
        assertThat(findToken).isEqualTo(refreshToken);
        assertThat(findToken.belongs(member)).isTrue();
    }

    @Test
    public void checkRefreshToken_jwt로_검색() throws Exception {
        //given
        Member member = createMember();
        RefreshToken refreshToken = new RefreshToken(member, "refreshToken1");
        refreshTokenRepository.save(refreshToken);

        //when
        Boolean isExist1 = refreshTokenRepository.existsByCode("refreshToken1");
        Boolean isExist2 = refreshTokenRepository.existsByCode("refreshToken2");
        //then
        assertThat(isExist1).isTrue();
        assertThat(isExist2).isFalse();
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