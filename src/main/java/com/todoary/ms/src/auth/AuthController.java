package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.dto.*;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.AppleUserInfo;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.src.user.dto.PatchPasswordReq;
import com.todoary.ms.src.user.dto.PostUserReq;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.ErrorLogWriter.writeExceptionWithMessage;
import static com.todoary.ms.util.ErrorLogWriter.writeExceptionWithRequest;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final UserProvider userProvider;
    private final AuthService authService;
    private final AuthProvider authProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AppleUtil appleUtil;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserService userService, UserProvider userProvider, AuthService authService, AuthProvider authProvider, AuthenticationManagerBuilder authenticationManagerBuilder, AppleUtil appleUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userProvider = userProvider;
        this.authService = authService;
        this.authProvider = authProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.appleUtil = appleUtil;
    }


    /**
     * 1.1 일반 로그인 api
     * [POST] /auth/signin
     *
     * @param postSigninReq
     * @return
     */
    @PostMapping("/signin")
    public BaseResponse<PostSigninRes> login(HttpServletRequest request, @RequestBody PostSigninReq postSigninReq) {
        if (postSigninReq.getFcm_token() == null) {
            return new BaseResponse<>(EMPTY_FCMTOKEN);
        }

        User user = null;
        try {
            user = userProvider.retrieveByEmail(postSigninReq.getEmail());
            user.setPassword(postSigninReq.getPassword());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postSigninReq.toString());
            return new BaseResponse<>(e.getStatus());
        }


        Authentication authentication = null;
        try {
            authentication = attemptAuthentication(user);
            checkFCMToken(user.getId(), user.getFcm_token(), postSigninReq.getFcm_token());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postSigninReq.toString());
            return new BaseResponse<>(e.getStatus());
        }


        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Long user_id = userEntity.getUser().getId();
        String accessToken = jwtTokenProvider.createAccessToken(user_id);
        Token token = new Token(accessToken, "");
        PostSigninRes postSigninRes = new PostSigninRes(token);

        return new BaseResponse<>(postSigninRes);
    }

    /**
     * 1.2 자동 로그인 api
     * [POST] /auth/signin/auto
     *
     * @param postAutoSigninReq
     * @return
     */
    @PostMapping("/signin/auto")
    public BaseResponse<PostAutoSigninRes> autoLogin(HttpServletRequest request, @RequestBody PostAutoSigninReq postAutoSigninReq) {
        if (postAutoSigninReq.getFcm_token() == null) {
            return new BaseResponse<>(EMPTY_FCMTOKEN);
        }

        User user = null;
        try {
            user = userProvider.retrieveByEmail(postAutoSigninReq.getEmail());
            user.setPassword(postAutoSigninReq.getPassword());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
            return new BaseResponse(e.getStatus());
        }

        Authentication authentication = null;
        try {
            authentication = attemptAuthentication(user);
            checkFCMToken(user.getId(), user.getFcm_token(), postAutoSigninReq.getFcm_token());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();

        Token token = null;
        try {
            token = authService.registerNewTokenForUser(userEntity.getUser().getId());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postAutoSigninReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
        PostAutoSigninRes postAutoSigninRes = new PostAutoSigninRes(token);
        return new BaseResponse<>(postAutoSigninRes);
    }

    /**
     * 1.3 토큰 재발급 api
     * [GET] /auth/jwt
     *
     * @param postAccessReq
     * @return
     */
    @PostMapping("/jwt")
    public BaseResponse<PostAccessRes> postAccess(HttpServletRequest request, @RequestBody PostAccessReq postAccessReq) {
        if (postAccessReq.getFcm_token() == null) {
            return new BaseResponse<>(EMPTY_FCMTOKEN);
        }

        String refreshToken = postAccessReq.getRefreshToken();
        try {
            AssertRefreshTokenEqualAndValid(refreshToken);
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request, postAccessReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }

        User user = null;
        try {
            Long user_id = Long.parseLong(jwtTokenProvider.getUserIdFromRefreshToken(refreshToken));
            user = userProvider.retrieveById(user_id);
            checkFCMToken(user_id, user.getFcm_token(), postAccessReq.getFcm_token());
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postAccessReq.toString());
            return new BaseResponse<>(e.getStatus());
        }

        try {
            Token newTokens = authService.registerNewTokenFromRefreshToken(refreshToken);
            PostAccessRes postAccessRes = new PostAccessRes(newTokens);
            return new BaseResponse<>(postAccessRes);
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request, postAccessReq.toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 1.4 구글 소셜 로그인 api
     * [GET] /oauth2/authorization/google
     */

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
        if (postUserReq.getFcm_token() == null) {
            return new BaseResponse<>(EMPTY_FCMTOKEN);
        }
        try {
            String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
            User user = new User(postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none", postUserReq.getFcm_token());
            userService.createUser(user, postUserReq.isTermsEnable());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, postUserReq.toString());
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
    public BaseResponse<BaseResponseStatus> PostSignupOauth2(HttpServletRequest request, @RequestBody PostSignupOauth2Req postSignupOauth2Req) {
        if (postSignupOauth2Req.getFcm_token() == null) {
            return new BaseResponse<>(EMPTY_FCMTOKEN);
        }

        try {
            userService.createOauth2User(postSignupOauth2Req);
            return new BaseResponse<>(SUCCESS);
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request, postSignupOauth2Req.toString());
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
            if (userProvider.checkEmail(email) == 0) { // 새 user
                return new BaseResponse<>("가능한 이메일입니다.");
            } else { // 이미 있는 유저
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
            }
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request);
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
    public BaseResponse<String> checkEmailExistence(HttpServletRequest request, @RequestParam(required = true) String email) {
        try {
            if (userProvider.checkEmail(email) == 1) { // email 확인
                return new BaseResponse<>("존재하는 일반 이메일 입니다.");
            } else { // 일반 email이 없는 경우
                return new BaseResponse<>(BaseResponseStatus.USERS_EMPTY_USER_EMAIL);
            }
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request);
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
    public BaseResponse<BaseResponseStatus> patchUserPassword(HttpServletRequest request, @RequestBody PatchPasswordReq patchPasswordReq) {
        try {
            userService.changePassword(patchPasswordReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithRequest(e, request, patchPasswordReq.toString());
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
    @GetMapping("/apple/redirect")
    public BaseResponse<GetAppleUserRes> Oauth2AppleLoginRedirect(HttpServletRequest request, @RequestParam("code")String code, @RequestParam("id_token")String id_token,@RequestParam(value= "user", required = false)String userInfo){
        PostSignupAppleReq postSignupAppleReq = new PostSignupAppleReq(code, id_token);
        AppleUserInfo appleUserInfo = null;
        String provider = "apple";
        String provider_id = null; //appleUniqueNo
        JSONObject tokenResponse = null;
        User user = null;
        Token token = null;
        GetAppleUserRes getAppleUserRes = null;
        String appleRefreshToken = null;

        /* create client_secret */
        try {
            String client_secret = appleUtil.createClientSecret();
            tokenResponse = appleUtil.validateAuthorizationGrantCode(client_secret,postSignupAppleReq.getCode());
        } catch (IOException e) {
            writeExceptionWithMessage(e, e.getMessage());
            return new BaseResponse<>(APPLE_Client_SECRET_ERROR);
        }

        /* decode id_token */
        if (tokenResponse.get("error") == null ) {
            JSONObject payload = appleUtil.decodeFromIdToken(tokenResponse.getAsString("id_token"));
            provider_id = payload.getAsString("sub");
            appleRefreshToken = tokenResponse.getAsString("refresh_token");
        }
        else return new BaseResponse<>(INVALID_APPLE_AUTH);

        /* DB 유저확인 */
        try {
            if (userProvider.checkAppleUniqueNo(provider_id) == 1)
                user = userProvider.retrieveByAppleUniqueNo(provider_id);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }

        if (user == null) {
            try {
                // 약관동의 처음
                if (userInfo != null) {
                    log.info("애플 로그인 최초입니다. 회원가입을 진행합니다.");
                    appleUserInfo = authService.parseUser(userInfo);
                    getAppleUserRes = new GetAppleUserRes(true, appleUserInfo.getName(),appleUserInfo.getEmail(),provider,provider_id,null,appleRefreshToken);
                }
                // 약관동의 취소 후 가입시
                else{
                    log.info("약관동의가 필요합니다.");
                    getAppleUserRes = new GetAppleUserRes(true, "","",provider,provider_id,null,appleRefreshToken);
                }
            } catch (BaseException e) {
                writeExceptionWithMessage(e, e.getMessage());
                return new BaseResponse<>(e.getStatus());
            }
        }
        else
        {
            log.info("애플 로그인 기록이 있습니다. 로그인을 진행합니다.");
            try {
                user = userProvider.retrieveByAppleUniqueNo(provider_id);
                token = authService.registerNewTokenForUser(user.getId());
            } catch (BaseException e) {
                writeExceptionWithMessage(e, e.getMessage());
                return new BaseResponse<>(e.getStatus());
            }
            getAppleUserRes = new GetAppleUserRes(false, user.getName(),user.getEmail(),provider,provider_id,token,appleRefreshToken);
        }
        return new BaseResponse<>(getAppleUserRes);
    }

    /**
     * 1.9.4 애플 회원 탈퇴 api
     * [POST] /auth/revoke/apple
     * @param request, code
     * @return token
     */
    @PostMapping("/revoke/apple")
    public BaseResponse<BaseResponseStatus> PostRevokeApple(HttpServletRequest request, @RequestBody String appleRefreshToken) {
        JSONObject tokenResponse = null;
        String appleAccessToken = null;

        /* create client_secret */
        try {
            String client_secret = appleUtil.createClientSecret();
            tokenResponse = appleUtil.validateAppleRefreshToken(client_secret,appleRefreshToken);
            /* decode id_token */
            if (tokenResponse.get("error") == null ) {
                appleAccessToken = tokenResponse.getAsString("access_token");
                appleUtil.revokeUser(client_secret,appleAccessToken);
                return new BaseResponse<>(SUCCESS);
            }
            else {
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
            writeExceptionWithMessage(e, "Refresh Token 만료 | "+ token);
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception e) {
            writeExceptionWithMessage(e, "Refresh Token 에러 | "+ token);
            throw new BaseException(INVALID_JWT);
        }
        if (!authProvider.isRefreshTokenEqual(token))
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

    public void checkFCMToken(Long user_id, String fcm_token, String input_fcm_token) throws BaseException {
        if (!fcm_token.equals(input_fcm_token)) {
            try {
                userService.modifyFcmToken(user_id, input_fcm_token);
            } catch (BaseException e) {
                throw new BaseException(MODIFY_FAIL_FCMTOKEN);
            }
        }
    }
}
