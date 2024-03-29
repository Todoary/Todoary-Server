package com.todoary.ms.src.legacy.auth;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.legacy.auth.dto.*;
import com.todoary.ms.src.legacy.auth.model.AppleUserInfo;
import com.todoary.ms.src.legacy.user.LegacyUserProvider;
import com.todoary.ms.src.legacy.user.LegacyUserService;
import com.todoary.ms.src.legacy.user.dto.PatchPasswordReq;
import com.todoary.ms.src.legacy.user.dto.PostUserReq;
import com.todoary.ms.src.legacy.user.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;
import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithMessage;

@Slf4j
//@RestController
@RequestMapping("/auth")
public class LegacyAuthController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final LegacyUserService legacyUserService;
    private final LegacyUserProvider legacyUserProvider;
    private final LegacyAuthService legacyAuthService;
    private final LegacyAuthProvider legacyAuthProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LegacyAppleUtil legacyAppleUtil;

    @Autowired
    public LegacyAuthController(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, LegacyUserService legacyUserService, LegacyUserProvider legacyUserProvider, LegacyAuthService legacyAuthService, LegacyAuthProvider legacyAuthProvider, AuthenticationManagerBuilder authenticationManagerBuilder, LegacyAppleUtil legacyAppleUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.legacyUserService = legacyUserService;
        this.legacyUserProvider = legacyUserProvider;
        this.legacyAuthService = legacyAuthService;
        this.legacyAuthProvider = legacyAuthProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.legacyAppleUtil = legacyAppleUtil;
    }


    /**
     * 1.1 일반 로그인 api
     * [POST] /auth/signin
     *
     * @param postSigninReq
     * @return
     */
//    @PostMapping("/signin")
//    public BaseResponse<PostSigninRes> login(HttpServletRequest request, @RequestBody PostSigninReq postSigninReq) {
//        User user = null;
//        try {
//            user = userProvider.retrieveByEmail(postSigninReq.getEmail());
//            user.setPassword(postSigninReq.getPassword());
//        } catch (BaseException e) {
//            writeExceptionWithRequest(e, request, postSigninReq.toString());
//            return new BaseResponse<>(e.getStatus());
//        }
//
//
//        Authentication authentication = null;
//        try {
//            authentication = attemptAuthentication(user);
//        } catch (BaseException e) {
//            writeExceptionWithRequest(e, request, postSigninReq.toString());
//            return new BaseResponse<>(e.getStatus());
//        }
//
//
//        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//        Long user_id = userEntity.getUser().getId();
//        String accessToken = jwtTokenProvider.createAccessToken(user_id);
//        Token token = new Token(accessToken, "");
//        PostSigninRes postSigninRes = new PostSigninRes(token);
//
//        return new BaseResponse<>(postSigninRes);
//    }

//    /**
//     * 1.2 자동 로그인 api
//     * [POST] /auth/signin/auto
//     *
//     * @param postAutoSigninReq
//     * @return
//     */
//    @PostMapping("/signin/auto")
//    public BaseResponse<PostAutoSigninRes> autoLogin(HttpServletRequest request,
//                                                     @RequestBody PostAutoSigninReq postAutoSigninReq) {
//        User user = null;
//        try {
//            user = userProvider.retrieveByEmail(postAutoSigninReq.getEmail());
//            user.setPassword(postAutoSigninReq.getPassword());
//        } catch (BaseException e) {
//            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
//            return new BaseResponse(e.getStatus());
//        }
//
//        Authentication authentication = null;
//        try {
//            authentication = attemptAuthentication(user);
//        } catch (BaseException e) {
//            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
//            return new BaseResponse<>(e.getStatus());
//        }
//        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
//
//        Token token = null;
//        try {
//            token = authService.registerNewTokenForUser(userEntity.getUser().getId());
//        } catch (BaseException e) {
//            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
//            return new BaseResponse<>(e.getStatus());
//        }
//        PostAutoSigninRes postAutoSigninRes = new PostAutoSigninRes(token);
//        return new BaseResponse<>(postAutoSigninRes);
//    }

    /**
     * 1.3 토큰 재발급 api
     * [GET] /auth/jwt
     *
     * @param postAccessReq
     * @return
     */
    @PostMapping("/jwt")
    public BaseResponse<PostAccessRes> postAccess(HttpServletRequest request,
                                                  @RequestBody PostAccessReq postAccessReq) {

        String refreshToken = postAccessReq.getRefreshToken();
        try {
            AssertRefreshTokenEqualAndValid(refreshToken);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        User user = null;
        try {
            Long user_id = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
            user = legacyUserProvider.retrieveById(user_id);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        try {
            Token newTokens = legacyAuthService.registerNewTokenFromRefreshToken(refreshToken);
            PostAccessRes postAccessRes = new PostAccessRes(newTokens);
            return new BaseResponse<>(postAccessRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.4 구글 소셜 로그인 api
     * [GET] /oauth2/authorization/google
     *
     * 위에는 웹으로 구현했으나 클라(안드로이드)에서 정보 가져오고 요청하는 것으로 변경.
     */
    @PostMapping("/signin/google")
    public BaseResponse<GoogleSigninResponse> signInIfGoogleUserSignedUp(HttpServletRequest servletRequest,
                                                                         @RequestBody GoogleSigninRequest request) {
        String provider = "google";
        log.error("구글 로그인 요청 {}", request.toString());
        try {
            if (legacyUserProvider.checkEmail(request.getEmail(), provider) == 1) {
                try {
                    User user = legacyUserProvider.retrieveByEmail(request.getEmail(), provider);
                    Token token = legacyAuthService.registerNewTokenForUser(user.getId());
                    return new BaseResponse<>(new GoogleSigninResponse(false, token));
                } catch (BaseException e) {
                    return new BaseResponse<>(e.getStatus());
                }
            } else {
                return new BaseResponse<>(new GoogleSigninResponse(true, null));
            }
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 1.5 애플 소셜 로그인 api
     * 미정
     */

    /**
     * 1.6 일반 회원가입 api
     * [POST] /auth/signup
     * 플로우: [1.4 사전 회원가입 api] 후에 클라이언트에서 약관 동의 후 호출됨
     *
     * @param postUserReq
     * @return 결과 메세지
     */
    @PostMapping("/signup")
    public BaseResponse<String> postUser(HttpServletRequest request, @RequestBody PostUserReq postUserReq) {
        try {
            String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
            User user = new User(postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none");
            legacyUserService.createUser(user, postUserReq.isTermsEnable());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 1.7 소셜 회원가입 api
     * [POST] /auth/signup/oauth2
     * 소셜 로그인 시도 후 새로운 유저라면 클라이언트가 약관 동의 후에
     * 이 api 호출하여 최종 회원가입
     */
    @PostMapping("/signup/oauth2")
    public BaseResponse<BaseResponseStatus> PostSignupOauth2(HttpServletRequest request,
                                                             @RequestBody PostSignupOauth2Req postSignupOauth2Req) {

        try {
            legacyUserService.createOauth2User(postSignupOauth2Req);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.8 이메일 중복체크 api
     * [GET] /email/duplication?email=
     *
     * @param email
     * @return
     */
    @GetMapping("/email/duplication")
    public BaseResponse<String> checkEmail(HttpServletRequest request, @RequestParam(required = true) String email) {
        try {
            if (legacyUserProvider.checkEmail(email) == 0) { // 새 user
                return new BaseResponse<>("가능한 이메일입니다.");
            } else { // 이미 있는 유저
                return new BaseResponse<>(BaseResponseStatus.MEMBERS_DUPLICATE_EMAIL);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.9.1 이메일 검증 api
     * [GET] /auth/email/existence?email=
     *
     * @param email
     * @return
     */
    @GetMapping("/email/existence")
    public BaseResponse<String> checkEmailExistence(HttpServletRequest request,
                                                    @RequestParam(required = true) String email) {
        try {
            if (legacyUserProvider.checkEmail(email) == 1) { // email 확인
                return new BaseResponse<>("존재하는 일반 이메일 입니다.");
            } else { // 일반 email이 없는 경우
                return new BaseResponse<>(BaseResponseStatus.USERS_EMPTY_USER_EMAIL);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.9.2 비밀번호 재설정 API
     * [PATCH] /auth/password
     *
     * @param request
     * @return
     */
    @PatchMapping("/password")
    public BaseResponse<BaseResponseStatus> patchUserPassword(HttpServletRequest request,
                                                              @RequestBody PatchPasswordReq patchPasswordReq) {
        try {
            legacyUserService.changePassword(patchPasswordReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 애플 로그인 리다이렉트 API
     * /auth/apple/redirect
     *
     * @param request, code, id_token, userInfo
     * @return token
     */
    @PostMapping("/apple/token")
    public BaseResponse<GetAppleUserRes> Oauth2AppleLoginRedirect(HttpServletRequest request,
                                                                  @RequestBody PostSignupAppleReq postSignupAppleReq) {
        AppleUserInfo appleUserInfo = new AppleUserInfo(postSignupAppleReq.getName(), postSignupAppleReq.getEmail());
        String provider = "apple";
        String provider_id = null; //appleUniqueNo
        JSONObject tokenResponse = null;
        User user = null;
        Token token = null;
        GetAppleUserRes getAppleUserRes = null;
        String appleRefreshToken = null;

        /* create client_secret */
        try {
            String client_secret = legacyAppleUtil.createClientSecret();
            tokenResponse = legacyAppleUtil.validateAuthorizationGrantCode(client_secret, postSignupAppleReq.getCode());
        } catch (IOException e) {
            return new BaseResponse<>(APPLE_Client_SECRET_ERROR);
        }

        /* decode id_token */
        if (tokenResponse.get("error") == null) {
            JSONObject payload = legacyAppleUtil.decodeFromIdToken(tokenResponse.getAsString("id_token"));
            provider_id = payload.getAsString("sub");
            appleRefreshToken = tokenResponse.getAsString("refresh_token");
        } else return new BaseResponse<>(INVALID_APPLE_AUTH);

        /* DB 유저확인 */
        try {
            if (legacyUserProvider.checkAppleUniqueNo(provider_id) == 1)
                user = legacyUserProvider.retrieveByAppleUniqueNo(provider_id);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if (user == null) {
            // 약관동의 처음
            log.info("애플 로그인 최초입니다. 회원가입을 진행합니다.");
            PostSignupOauth2Req postSignupOauth2Req = new PostSignupOauth2Req(appleUserInfo.getName(), appleUserInfo.getEmail(), provider, provider_id, postSignupAppleReq.isTermsEnable());
            try {
                Long userId = legacyUserService.createAppleUser(postSignupOauth2Req);
                token = legacyAuthService.registerNewTokenForUser(userId);
                getAppleUserRes = new GetAppleUserRes(true, appleUserInfo.getName(), appleUserInfo.getEmail(), provider, provider_id, token, appleRefreshToken);
            } catch (BaseException exception) {
                return new BaseResponse<>(exception.getStatus());
            }
        } else {
            log.info("애플 로그인 기록이 있습니다. 로그인을 진행합니다.");
            try {
                user = legacyUserProvider.retrieveByAppleUniqueNo(provider_id);
                token = legacyAuthService.registerNewTokenForUser(user.getId());
            } catch (BaseException e) {
                writeExceptionWithMessage(e, e.getMessage());
                return new BaseResponse<>(e.getStatus());
            }
            getAppleUserRes = new GetAppleUserRes(false, user.getName(), user.getEmail(), provider, provider_id, token, appleRefreshToken);
        }
        return new BaseResponse<>(getAppleUserRes);
    }

    /**
     * 1.9.4 애플 회원 탈퇴 api
     * [POST] /auth/revoke/apple
     *
     * @param request, code
     * @return token
     */
    @PostMapping("/revoke/apple")
    public BaseResponse<BaseResponseStatus> PostRevokeApple(HttpServletRequest request,
                                                            @RequestBody PostRevokeAppleReq postRevokeAppleReq) {
        JSONObject tokenResponse = null;
        String appleAccessToken = null;
        /* create client_secret */
        try {
            String client_secret = legacyAppleUtil.createClientSecret();
            tokenResponse = legacyAppleUtil.validateAuthorizationGrantCode(client_secret, postRevokeAppleReq.getCode());
            /* decode id_token */
            if (tokenResponse.get("error") == null) {
                appleAccessToken = tokenResponse.getAsString("access_token");
                legacyAppleUtil.revokeUser(client_secret, appleAccessToken);
                try {
                    legacyUserService.removeAppleUser(postRevokeAppleReq.getEmail());
                } catch (BaseException e) {
                    writeExceptionWithMessage(e, e.getMessage());
                    return new BaseResponse<>(e.getStatus());
                }
                return new BaseResponse<>(SUCCESS);
            } else {
                return new BaseResponse<>(INVALID_APPLE_AUTH);
            }
        } catch (IOException e) {
            writeExceptionWithMessage(e, e.getMessage());
            return new BaseResponse<>(APPLE_Client_SECRET_ERROR);
        }
    }

    public void AssertRefreshTokenEqualAndValid(String token) throws BaseException {
        try {
            Jwts
                    .parserBuilder().setSigningKey(jwtTokenProvider.getRefreshKey()).build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            writeExceptionWithMessage(e, "Refresh Token 만료 | " + token);
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception e) {
            writeExceptionWithMessage(e, "Refresh Token 에러 | " + token);
            throw new BaseException(INVALID_JWT);
        }
        if (!legacyAuthProvider.isRefreshTokenEqual(token))
            throw new BaseException(DIFFERENT_REFRESH_TOKEN);
    }

    public Authentication attemptAuthentication(User user) throws BaseException {
        Collection<GrantedAuthority> userAuthorities = new ArrayList<>();
        userAuthorities.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), userAuthorities);
        try {
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e) {
            throw new BaseException(USERS_DISACCORD_PASSWORD);
        }
    }

}
