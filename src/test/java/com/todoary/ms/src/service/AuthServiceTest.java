package com.todoary.ms.src.service;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class AuthServiceTest {
    @Autowired
    EntityManager em;

    @Autowired
    AuthService authService;

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
    void refreshToken이_존재하는_멤버에게_새로_저장할_경우() throws NoSuchFieldException, InterruptedException {
        // 멤버에게 새로운 refreshToken 저장
        Member member = createMemberHasRefreshToken();
        String beforeRefreshTokenCode = member.getRefreshToken().getCode();

        // 추가로 refreshToken 저장
        // sleep하지 않으면 시간 차이가 얼마 나지 않아 refresh token 값이 같음 (밀리 세컨드 단위는 없어진다)
        Thread.sleep(1000);
        RefreshToken found = authService.createRefreshToken(member);
        RefreshToken findRefreshToken = refreshTokenService.findByMemberId(member.getId());

        assertThat(beforeRefreshTokenCode).isNotEqualTo(findRefreshToken.getCode());
        assertThat(found.getCode()).isEqualTo(findRefreshToken.getCode());
    }

    @Test
    void refreshToken이_존재하지_않는_멤버에게_새로_저장할_경우() throws NoSuchFieldException {
        Member member = createMember();

        authService.createRefreshToken(member);
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
        AccessToken accessToken = authService.issueAccessToken(member.getId());
        RefreshToken refreshToken = authService.issueRefreshToken(member.getId());

        //then
        assertThat(authService.decodableAccessToken(accessToken.getCode(), member.getId())).isTrue();
        assertThat(authService.decodableRefreshToken(refreshToken.getCode(), member.getId())).isTrue();
    }

    @Test
    public void refreshToken으로_accessToken과_refreshToken발행() throws Exception {
        //given
        Member member = createMemberHasRefreshToken();
        RefreshToken findRefreshToken = refreshTokenService.findByMemberId(member.getId());

        //when
        AccessToken accessToken = authService.issueAccessToken(findRefreshToken.getCode());
        RefreshToken refreshToken = authService.issueRefreshToken(findRefreshToken.getCode());

        //then
        assertThat(authService.decodableAccessToken(accessToken.getCode(), member.getId())).isTrue();
        assertThat(authService.decodableRefreshToken(refreshToken.getCode(), member.getId())).isTrue();
    }

    Member createMember() {
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        return memberService.findById(memberService.joinGeneralMember(memberJoinParam));
    }

    Member createMemberHasRefreshToken() {
        Member member = createMember();

        RefreshToken refreshToken = new RefreshToken(member, jwtTokenProvider.createRefreshToken(member.getId()));
        refreshTokenService.save(refreshToken);
        return member;
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