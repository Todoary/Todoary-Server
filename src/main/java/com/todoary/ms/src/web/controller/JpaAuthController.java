package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.service.AppleAuthService;
import com.todoary.ms.src.service.JpaAuthService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.*;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/jpa")
public class JpaAuthController {
    private final JpaAuthService authService;
    private final MemberService memberService;
    private final AppleAuthService appleAuthService;

    /**
     * 1.1 일반 로그인 api
     * [POST] /auth/signin
     *
     * @param signinRequest
     * @return
     */
    @PostMapping("/signin")
    public BaseResponse<SigninResponse> login(@RequestBody SigninRequest signinRequest) {
        Long memberId = authService.authenticate(signinRequest.getEmail(), signinRequest.getPassword());
        return new BaseResponse<>(new SigninResponse(authService.issueAccessToken(memberId).getCode(), ""));
    }

    /**
     * 1.2 자동 로그인 api
     * [POST] /auth/signin/auto
     *
     * @param autoSigninRequest
     * @return
     */
    @PostMapping("/signin/auto")
    public BaseResponse<AutoSigninResponse> autoLogin(@RequestBody AutoSigninRequest autoSigninRequest) {
        Long memberId = authService.authenticate(autoSigninRequest.getEmail(), autoSigninRequest.getPassword());
        return new BaseResponse<>(new AutoSigninResponse(authService.issueAccessToken(memberId).getCode(), authService.issueRefreshToken(memberId).getCode()));
    }

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
        AccessToken accessToken = authService.issueAccessToken(refreshTokenCode);
        RefreshToken refreshToken = authService.issueRefreshToken(refreshTokenCode);

        return new BaseResponse<>(new AuthenticationTokenIssueResponse(accessToken.getCode(), refreshToken.getCode()));
    }

    public void validateMemberByRefreshToken(String refreshTokenCode) {
        memberService.validateMemberByRefreshToken(refreshTokenCode);
    }

    public void validateRefreshToken(String refreshTokenCode) {
        authService.validateRefreshToken(refreshTokenCode);
    }

    /**
     * 1.6 일반 회원가입 api
     * [POST] /auth/signup
     * 플로우: [1.4 사전 회원가입 api] 후에 클라이언트에서 약관 동의 후 호출됨
     *
     * @param memberJoinRequest
     * @return 결과 메세지
     */
    @PostMapping("/signup")
    public BaseResponse<BaseResponseStatus> joinNormalMember(@RequestBody MemberJoinRequest memberJoinRequest) {
        String encodedPassword = memberService.encodePassword(memberJoinRequest.getPassword());
        MemberJoinParam memberJoinParam = new MemberJoinParam(
                memberJoinRequest.getName(),
                memberJoinRequest.getNickname(),
                memberJoinRequest.getEmail(),
                encodedPassword,
                "ROLE_USER",
                memberJoinRequest.getIsTermsEnable()
        );
        memberService.joinGeneralMember(memberJoinParam);
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    /**
     * 1.8 이메일 중복체크 api
     * [GET] /email/duplication?email=
     *
     * @param email
     * @return
     */
    @GetMapping("/email/duplication")
    public BaseResponse<String> checkEmailDuplication(@RequestParam(required = true) String email) {
        memberService.checkEmailDuplication(email);

        return new BaseResponse<>("가능한 이메일입니다.");
    }

    /**
     * 1.9.2 비밀번호 재설정 API
     * [PATCH] /auth/password
     *
     * @param memberPasswordChangeRequest
     * @return
     */
    @PatchMapping("/password")
    public BaseResponse<BaseResponseStatus> patchUserPassword(@RequestBody MemberPasswordChangeRequest memberPasswordChangeRequest) {
        memberService.changePassword(memberPasswordChangeRequest.getEmail(), memberPasswordChangeRequest.getNewPassword());
        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/apple/signin")
    public BaseResponse<AppleSigninResponse> apple(@RequestBody AppleSigninRequest appleSigninRequest) {
        // validate code
        JSONObject tokenResponse = appleAuthService.getTokenResponseByCode(appleSigninRequest.getCode());

        // validate idToken
        String idToken = tokenResponse.getAsString("id_token");
        String providerId = appleAuthService.getProviderIdFrom(idToken);
        String appleRefreshToken = tokenResponse.getAsString("refresh_token");

        // member existence check by providerId
        ProviderAccount providerAccount = ProviderAccount.appleFrom(providerId);
        boolean memberExists = memberService.existsByProviderAccount(providerAccount);

        // issue tokens
        Long memberId;
        if (memberExists) {
            memberId = memberService.findByProviderEmail(
                    appleSigninRequest.getEmail(),
                    providerAccount.getProvider().toString()
            ).getId();
        } else {
            memberId = memberService.joinOauthMember(new OauthMemberJoinParam(
                    appleSigninRequest.getName(),
                    appleSigninRequest.getEmail(),
                    providerAccount,
                    "ROLE_USER",
                    appleSigninRequest.isTermsEnable()
            ));
        }

        return new BaseResponse<>(new AppleSigninResponse(
                !memberExists,
                appleSigninRequest.getName(),
                appleSigninRequest.getEmail(),
                providerAccount,
                authService.issueAccessToken(memberId).getCode(),
                authService.issueRefreshToken(memberId).getCode(),
                appleRefreshToken
        ));
    }
}
