package com.todoary.ms.src.service;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.common.auth.PrincipalDetails;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.web.dto.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public void validateRefreshToken(String refreshTokenCode) {
        jwtTokenProvider.validateRefreshToken(refreshTokenCode);
    }

    @Transactional(readOnly = true)
    public Boolean decodableRefreshToken(String refreshTokenCode, Long memberId) throws Exception {
        return memberId == Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode));
    }

    @Transactional(readOnly = true)
    public Boolean decodableAccessToken(String accessTokenCode, Long memberId) throws Exception {
        return memberId == Long.parseLong(jwtTokenProvider.getUserIdFromAccessToken(accessTokenCode));
    }

    public Long authenticate(String email, String password) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            //PrincipalDetailsService::loadUserByUsername
            Authentication authentication = getAuthentication(authenticationToken);
            return ((PrincipalDetails) authentication.getPrincipal())
                    .getMember()
                    .getId();
        } catch (BadCredentialsException badCredentialsException) {
            // credential, 비밀번호가 일치하지 않을 때 예외 발생
            throw new TodoaryException(USERS_DISACCORD_PASSWORD);
        } catch (AuthenticationException authenticationException) {
            // authenticate 실패했을 때 예외 발생
            throw new TodoaryException(USERS_AUTHENTICATION_FAILURE);
        }
    }

    // authService의 통제 밖인 authenticationManger(authenticationManagerBuilder.getObject())를 테스트하기 위해 메서드 분리
    public Authentication getAuthentication(UsernamePasswordAuthenticationToken authenticationToken)
            throws BadCredentialsException, AuthenticationException{
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException badCredentialsException) {
            throw badCredentialsException;
        } catch (AuthenticationException authenticationException) {
            throw authenticationException;
        }
    }

    public Token issueTokens(Long memberId) {
        memberService.checkMemberExistsById(memberId);
        return Token.builder()
                .accessToken(createAccessToken(memberId).getCode())
                .refreshToken(createRefreshToken(memberId).getCode())
                .build();
    }

    public AccessToken issueAccessToken(Long memberId) {
        memberService.checkMemberExistsById(memberId);
        return createAccessToken(memberId);
    }

    public RefreshToken issueRefreshToken(Long memberId) {
        memberService.checkMemberExistsById(memberId);
        return createRefreshToken(memberId);
    }
    private AccessToken createAccessToken(Long memberId) {
        return new AccessToken(jwtTokenProvider.createAccessToken(memberId));
    }

    private RefreshToken createRefreshToken(Long memberId) {
        Member member = memberService.findMemberById(memberId);
        return createRefreshToken(member);
    }

    private RefreshToken createRefreshToken(Member member) {
        String code = jwtTokenProvider.createRefreshToken(member.getId());
        if (member.hasRefreshToken()) {
            return member.updateRefreshToken(code);
        } else {
            RefreshToken refreshToken = new RefreshToken(member, code);
            refreshTokenService.save(refreshToken);
            return refreshToken;
        }
    }

    public AccessToken issueAccessTokenFromRefreshTokenCode(String refreshTokenCode) {
        return issueAccessToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }

    public RefreshToken issueRefreshTokenFromRefreshTokenCode(String refreshTokenCode) {
        return issueRefreshToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }

}
