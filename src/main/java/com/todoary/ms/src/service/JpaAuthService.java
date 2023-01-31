package com.todoary.ms.src.service;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.AuthenticationToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.todoary.ms.util.BaseResponseStatus.*;

@RequiredArgsConstructor
@Transactional
@Service
public class JpaAuthService {
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


    public RefreshToken saveRefreshToken(Member member) {
        RefreshToken refreshToken = new RefreshToken(member, jwtTokenProvider.createRefreshToken(member.getId()));

        refreshTokenService.save(refreshToken);
        return refreshToken;
    }

    public AuthenticationToken issueAuthenticationToken(Long memberId) {
        Member findMember = memberService.findById(memberId);

        AccessToken accessToken = new AccessToken(jwtTokenProvider.createAccessToken(memberId));
        RefreshToken refreshToken = saveRefreshToken(findMember);

        return new AuthenticationToken(accessToken.getCode(), refreshToken.getCode());
    }

    public AuthenticationToken issueAuthenticationToken(String refreshTokenCode) {
        return issueAuthenticationToken(Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshTokenCode)));
    }

    public AuthenticationToken issueAuthenticationToken(Authentication authentication) {
        Long memberId = ((PrincipalDetails) authentication.getPrincipal()).getMember().getId();
        return new AuthenticationToken(jwtTokenProvider.createAccessToken(memberId), "");
    }

    public Authentication authenticate(String email, String password) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);

            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (BadCredentialsException badCredentialsException) {
            // credential, 비밀번호가 일치하지 않을 때 예외 발생
            throw new TodoaryException(USERS_DISACCORD_PASSWORD);
        } catch (AuthenticationException authenticationException) {
            // authenticate 실패했을 때 예외 발생
            throw new TodoaryException(USERS_AUTHENTICATION_FAILURE);
        }
    }
}
