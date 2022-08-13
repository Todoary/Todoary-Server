package com.todoary.ms.src.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.json.Json;
import com.todoary.ms.src.auth.dto.*;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
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
        String refreshToken = postAccessReq.getRefreshToken();
        try {
            AssertRefreshTokenEqualAndValid(refreshToken);
        } catch (BaseException exception) {
            writeExceptionWithRequest(exception, request, postAccessReq.toString());
            return new BaseResponse<>(exception.getStatus());
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
        try {
            String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
            User user = new User(postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none");
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

    @RequestMapping("/apple/redirect")
    public BaseResponse<BaseResponseStatus> Oauth2AppleLoginRedirect(HttpServletRequest request, @RequestBody PostSignupAppleReq postSignupAppleReq){
        String client_secret = null;
        String provider = "apple";

        try {
            client_secret = appleUtil.createClientSecret();
        } catch (IOException e) {
            writeExceptionWithRequest(e, request, postSignupAppleReq.toString());
            return new BaseResponse<>(APPLE_Client_SECRET_ERROR);
        }

        JSONObject tokenResponse = appleUtil.validateAuthorizationGrantCode(client_secret,postSignupAppleReq.getCode());

        if (tokenResponse.get("error") == null ) {
            JSONObject payload = appleUtil.decodeFromIdToken(tokenResponse.getAsString("id_token"));
            String appleUniqueNo = payload.getAsString("sub");
            String email = payload.getAsString("email");
            User user = null;

            try {
                if (userProvider.checkEmail(email, provider) == 1)
                    user = userProvider.retrieveByEmail(email, provider);
            } catch (BaseException e) {
                throw new RuntimeException(e);
            }

            // TODO: 로그인 회원가입 로직 처리
            if (user == null) {
                log.info("애플 로그인 최초입니다. 회원가입을 진행합니다.");
                user = new User("", "", email, "", "", provider, appleUniqueNo);

            } else {
                log.info("애플 로그인 기록이 있습니다. 로그인을 진행합니다.");
            }

        }
        else {
            return new BaseResponse<>(INVALID_APPLE_AUTH);
        }

        return new BaseResponse<>(BaseResponseStatus.SUCCESS);
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
}
