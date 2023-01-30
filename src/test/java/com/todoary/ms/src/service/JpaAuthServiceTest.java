package com.todoary.ms.src.service;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.AuthenticationToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
class JpaAuthServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    JpaAuthService authService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    MemberService memberService;

    @Autowired
    RefreshTokenService refreshTokenService;
    /**
     * decodable()
     */
    @Test
    void refreshToken에서_멤버_id_decode가_일치() throws Exception {
        Member member = createMember();

        String refreshTokenCode = jwtTokenProvider.createRefreshToken(member.getId());
        assertThat(authService.decodableRefreshToken(refreshTokenCode, member.getId())).isTrue();
    }

    @Test
    void accessToken에서_멤버_id_decode가_일치() throws Exception {
        Member member = createMember();

        String accessTokenCode = jwtTokenProvider.createAccessToken(member.getId());
        assertThat(authService.decodableAccessToken(accessTokenCode, member.getId())).isTrue();
    }

    /**
     * saveRefreshToken()
     */
    @Test
    void refreshToken이_존재하는_멤버에게_새로_저장할_경우() throws NoSuchFieldException {
        // 멤버에게 새로운 refreshToken 저장
        Member member = createMemberHasRefreshToken();
        RefreshToken originRefreshToken = member.getRefreshToken();

        // 추가로 refreshToken 저장
        authService.saveRefreshToken(member);
        RefreshToken findRefreshToken = refreshTokenService.findByMemberId(member.getId());

        assertThat(member.getRefreshToken()).isEqualTo(findRefreshToken);
    }

    @Test
    void refreshToken이_존재하지_않는_멤버에게_새로_저장할_경우() throws NoSuchFieldException {
        Member member = createMember();

        authService.saveRefreshToken(member);
        RefreshToken findRefreshToken = refreshTokenService.findByMemberId(member.getId());

        assertThat(member.getRefreshToken()).isEqualTo(findRefreshToken);
    }

    /**
     * issueAuthenticationToken()
     */
    @Test
    public void 멤버_id로_accessToken과_refreshToken발행() throws Exception {
        //given
        Member member = createMember();

        //when
        AuthenticationToken authenticationToken = authService.issueAuthenticationToken(member.getId());

        //then
        assertThat(authService.decodableAccessToken(authenticationToken.getAccessToken(), member.getId())).isTrue();
        assertThat(authService.decodableRefreshToken(authenticationToken.getRefreshToken(), member.getId())).isTrue();
    }

    @Test
    public void refreshToken으로_accessToken과_refreshToken발행() throws Exception {
        //given
        Member member = createMemberHasRefreshToken();
        RefreshToken refreshToken = refreshTokenService.findByMemberId(member.getId());

        //when
        AuthenticationToken authenticationToken = authService.issueAuthenticationToken(refreshToken.getCode());

        //then
        assertThat(authService.decodableAccessToken(authenticationToken.getAccessToken(), member.getId())).isTrue();
        assertThat(authService.decodableRefreshToken(authenticationToken.getRefreshToken(), member.getId())).isTrue();
    }

    Member createMember() {
        Member member = Member.builder()
                .build();
        memberService.join(member);
        return member;
    }

    Member createMemberHasRefreshToken() {
        Member member = Member.builder()
                .build();
        memberService.join(member);

        RefreshToken refreshToken = new RefreshToken(member, jwtTokenProvider.createRefreshToken(member.getId()));
        refreshTokenService.save(refreshToken);
        return member;
    }
}