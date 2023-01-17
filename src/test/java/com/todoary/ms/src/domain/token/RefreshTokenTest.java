package com.todoary.ms.src.domain.token;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class RefreshTokenTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void 토큰_변경시에_토큰만_수정() {
        RefreshToken refreshToken = createRefreshToken("token1");
        Member member = refreshToken.getMember();
        refreshTokenRepository.save(refreshToken);
        System.out.println("1 : 1차");
        em.flush();
        em.clear();
        // 토큰 수정
        member.getRefreshToken().changeCode("token2");
        refreshTokenRepository.save(refreshToken);
    }

    @Test
    void 토큰_변경시에_새로운_인스턴스로_대체() {
        RefreshToken refreshToken = createRefreshToken("token1");
        refreshTokenRepository.save(refreshToken);
        System.out.println("1 : 1차");

        // 토큰 수정
        RefreshToken newRefreshToken = new RefreshToken(refreshToken.getMember(), "token2");
        refreshTokenRepository.save(newRefreshToken);

    }

    Member createMember() {
        Member member = Member.builder()
                .build();

        em.persist(member);
        return member;
    }

    RefreshToken createRefreshToken(String token) {
        return new RefreshToken(createMember(), token);
    }
}