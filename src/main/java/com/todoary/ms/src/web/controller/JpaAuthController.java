package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.auth.dto.PostSigninReq;
import com.todoary.ms.src.auth.dto.PostSigninRes;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.src.domain.token.AuthenticationToken;
import com.todoary.ms.src.service.JpaAuthService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.src.web.dto.AuthenticationTokenIssueResponse;
import com.todoary.ms.src.web.dto.RefreshTokenIssueRequest;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.todoary.ms.util.ErrorLogWriter.writeExceptionWithRequest;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/jpa")
public class JpaAuthController {
    private final JpaAuthService jpaAuthService;
    private final MemberService memberService;

    /**
     * 1.3 토큰 재발급 api
     * [GET] /auth/jwt
     *
     * @param refreshTokenIssueRequest
     * @return
     */
    @PostMapping("/jwt")
    public BaseResponse<AuthenticationTokenIssueResponse> reIssueAuthenticationToken(@RequestBody RefreshTokenIssueRequest refreshTokenIssueRequest) {
        String refreshTokenCode = refreshTokenIssueRequest.getRefreshToken();

        // refreshTokenCode 검증
        validateRefreshToken(refreshTokenCode);
        validateMemberByRefreshToken(refreshTokenCode);

        // accessToken, refreshToken 재발급
        AuthenticationToken authenticationToken = jpaAuthService.issueAuthenticationToken(refreshTokenCode);
        return new BaseResponse<>(new AuthenticationTokenIssueResponse(authenticationToken));
    }

    public void validateMemberByRefreshToken(String refreshTokenCode) {
        memberService.validateMemberByRefreshToken(refreshTokenCode);
    }

    public void validateRefreshToken(String refreshTokenCode) {
        jpaAuthService.validateRefreshToken(refreshTokenCode);
    }
}
