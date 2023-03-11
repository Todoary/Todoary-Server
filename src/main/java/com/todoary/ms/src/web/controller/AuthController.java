package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.service.AppleAuthService;
import com.todoary.ms.src.service.AuthService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
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
        Long memberId = authenticateGeneralMember(signinRequest.getEmail(), signinRequest.getPassword());
        return new BaseResponse<>(new SigninResponse(new Token(authService.issueAccessToken(memberId).getCode(), "")));
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
        Long memberId = authenticateGeneralMember(autoSigninRequest.getEmail(), autoSigninRequest.getPassword());
        return new BaseResponse<>(new AutoSigninResponse(new Token(authService.issueAccessToken(memberId).getCode(), authService.issueRefreshToken(memberId).getCode())));
    }

    private Long authenticateGeneralMember(String email, String password) {
        if (!memberService.existsByGeneralEmail(email)) {
            throw new TodoaryException(USERS_EMPTY_USER_EMAIL);
        }
        if (memberService.existsDeactivatedGeneralMemberByEmail(email)) {
            throw new TodoaryException(EMAIL_USED_BY_DEACTIVATED_MEMBER);
        }
        return authService.authenticate(email, password);
    }

    /**
     * 1.3 토큰 재발급 api
     * [GET] /auth/jwt
     *
     * @param refreshTokenIssueRequest
     * @return
     */
    @PostMapping("/jwt")
    public BaseResponse<AuthenticationTokenIssueResponse> reIssueAuthenticationToken(
            @RequestBody RefreshTokenIssueRequest refreshTokenIssueRequest) {
        String refreshTokenCode = refreshTokenIssueRequest.getRefreshToken();

        // refreshTokenCode 검증
        validateRefreshToken(refreshTokenCode);
        validateMemberByRefreshToken(refreshTokenCode);

        // accessToken, refreshToken 재발급
        AccessToken accessToken = authService.issueAccessToken(refreshTokenCode);
        RefreshToken refreshToken = authService.issueRefreshToken(refreshTokenCode);

        return new BaseResponse<>(new AuthenticationTokenIssueResponse(new Token(accessToken.getCode(), refreshToken.getCode())));
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
        return new BaseResponse<>(SUCCESS);
    }

    /**
     * 1.7 소셜 회원가입 api
     * [POST] /auth/signup/oauth2
     * 소셜 로그인 시도 후 새로운 유저라면 클라이언트가 약관 동의 후에
     * 이 api 호출하여 최종 회원가입
     */
    @PostMapping("/signup/oauth2")
    public BaseResponse<BaseResponseStatus> PostSignupOauth2(@RequestBody OauthMemberJoinRequest request) {
        OauthMemberJoinParam memberJoinParam = new OauthMemberJoinParam(
                request.getName(),
                request.getEmail(),
                ProviderAccount.from(request.getProvider(), request.getProviderId()),
                "ROLE_USER",
                request.isTermsEnable()
        );

        memberService.joinOauthMember(memberJoinParam);

        return new BaseResponse<>(SUCCESS);
    }

    /**
     * 1.8 이메일 중복체크 api
     * [GET] /email/duplication?email=
     */
    @GetMapping("/email/duplication")
    public BaseResponse<BaseResponseStatus> checkEmailDuplication(@RequestParam String email) {
        memberService.checkEmailDuplicationOfGeneral(email);
        if (!memberService.existsByGeneralEmail(email)) {
            return BaseResponse.from(SUCCESS);
        }
        if (memberService.isEmailUsedByDeactivatedGeneralMember(email)) {
            return BaseResponse.from(EMAIL_USED_BY_DEACTIVATED_MEMBER);
        }
        return BaseResponse.from(MEMBERS_DUPLICATE_EMAIL);
    }

    /**
     * 1.9.1 이메일 검증 api
     * [GET] /auth/email/existence?email=
     * 비밀번호 찾기 시 이메일 검증 위해 사용됨
     * 탈퇴한 멤버든 아니든 비밀번호 찾기 가능
     */
    @GetMapping("/email/existence")
    public BaseResponse<String> checkEmailExistence(@RequestParam String email) {
        if (memberService.existsByGeneralEmail(email)) {
            return new BaseResponse<>("존재하는 일반 이메일입니다.");
        }
        return new BaseResponse<>(USERS_EMPTY_USER_EMAIL);
    }

    /**
     * 1.9.2 비밀번호 재설정 API
     * [PATCH] /auth/password
     *
     * @param memberPasswordChangeRequest
     * @return
     */
    @PatchMapping("/password")
    public BaseResponse<BaseResponseStatus> patchUserPassword(
            @RequestBody MemberPasswordChangeRequest memberPasswordChangeRequest) {
        memberService.changePassword(memberPasswordChangeRequest.getEmail(), memberPasswordChangeRequest.getNewPassword());
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/apple/token")
    public BaseResponse<AppleSigninResponse> appleSignin(@RequestBody AppleSigninRequest appleSigninRequest) {
        // validate code
        JSONObject tokenResponse = appleAuthService.getTokenResponseByCode(appleSigninRequest.getCode());

        // validate idToken
        String idToken = tokenResponse.getAsString("id_token");
        String providerId = appleAuthService.getProviderIdFrom(idToken);
        String appleRefreshToken = tokenResponse.getAsString("refresh_token");

        // member existence check by providerId
        ProviderAccount providerAccount = ProviderAccount.appleFrom(providerId);

        boolean memberExists = false, memberDeactivated = false;
        Optional<Member> member = memberService.findMemberOrEmptyByProviderAccount(providerAccount);
        Long memberId;
        if (member.isEmpty()) {
            memberId = memberService.joinOauthMember(new OauthMemberJoinParam(
                    appleSigninRequest.getName(),
                    appleSigninRequest.getEmail(),
                    providerAccount,
                    "ROLE_USER",
                    appleSigninRequest.isTermsEnable()
            ));
        } else {
            memberExists = true;
            memberDeactivated = member.get().isDeactivated();
            memberId = member.get().getId();
        }
        return new BaseResponse<>(new AppleSigninResponse(
                !memberExists,
                memberDeactivated,
                appleSigninRequest.getName(),
                appleSigninRequest.getEmail(),
                providerAccount.getProvider().name(),
                providerAccount.getProviderId(),
                new Token(authService.issueAccessToken(memberId).getCode(), authService.issueRefreshToken(memberId).getCode()),
                appleRefreshToken
        ));
    }

    @PostMapping("/revoke/apple")
    public BaseResponse<BaseResponseStatus> appleRevoke(@RequestBody AppleRevokeRequest appleRevokeRequest) {
        // revoke from Apple
        JSONObject tokenResponse = appleAuthService.getTokenResponseByCode(appleRevokeRequest.getCode());
        String appleRefreshToken = tokenResponse.getAsString("refresh_token");
        appleAuthService.revoke(appleRefreshToken);

        // revoke from Todoary
        String providerId = appleAuthService.getProviderIdFrom(tokenResponse.getAsString("id_token"));
        Member member = memberService.findActiveMemberByProviderAccount(ProviderAccount.appleFrom(providerId));
        memberService.deactivateMember(member);

        return new BaseResponse<>(SUCCESS);
    }

    @PatchMapping("/restore")
    public BaseResponse<BaseResponseStatus> activateMember(@RequestBody @Valid RestoreRequest request) {
        memberService.activateMember(
                request.getEmail(), ProviderAccount.of(request.getProvider(), request.getProviderId()));
        return BaseResponse.from(SUCCESS);
    }
}
