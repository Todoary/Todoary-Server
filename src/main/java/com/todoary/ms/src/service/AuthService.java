package com.todoary.ms.src.service;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.common.auth.PrincipalDetails;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.common.exception.TodoaryException;
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

    public RefreshToken createRefreshToken(Member member) {
        String code = jwtTokenProvider.createRefreshToken(member.getId());
        if (member.hasRefreshToken()) {
            return member.updateRefreshToken(code);
        } else {
            RefreshToken refreshToken = new RefreshToken(member, code);
            refreshTokenService.save(refreshToken);
            return refreshToken;
        }
    }

    public Long authenticate(String email, String password) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            //PrincipalDetailsService::loadUserByUsername
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
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

    public AccessToken issueAccessToken(Long memberId) {
        Member findMember = memberService.findById(memberId);

        return new AccessToken(jwtTokenProvider.createAccessToken(memberId));
    }

    public AccessToken issueAccessToken(String refreshTokenCode) {
        return issueAccessToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }

    public RefreshToken issueRefreshToken(Long memberId) {
        Member findMember = memberService.findById(memberId);
        RefreshToken refreshToken = createRefreshToken(findMember);

        return refreshToken;
    }

    public RefreshToken issueRefreshToken(String refreshTokenCode) {
        return issueRefreshToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }
}
