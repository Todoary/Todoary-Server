package com.todoary.ms.src.auth;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

import static com.todoary.ms.util.BaseResponseStatus.*;

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

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserService userService, UserProvider userProvider, AuthService authService, AuthProvider authProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.userProvider = userProvider;
        this.authService = authService;
        this.authProvider = authProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }


    /**
     * 1.1 일반 로그인 api
     * [POST] /auth/signin
     *
     * @param postSigninReq
     * @return
     */
    @PostMapping("/signin")
    public BaseResponse<PostSigninRes> login(@RequestBody PostSigninReq postSigninReq) {
        User user = null;
        try {
            user = userProvider.retrieveByEmail(postSigninReq.getEmail());
            user.setPassword(postSigninReq.getPassword());
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }


        Authentication authentication = null;
        try {
            authentication = attemptAuthentication(user);
        } catch (BaseException e) {
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
    public BaseResponse<PostAutoSigninRes> autoLogin(@RequestBody PostAutoSigninReq postAutoSigninReq) {
        User user = null;
        try {
            user = userProvider.retrieveByEmail(postAutoSigninReq.getEmail());
            user.setPassword(postAutoSigninReq.getPassword());
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
        Authentication authentication = null;
        try {
            authentication = attemptAuthentication(user);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
        PrincipalDetails userEntity = (PrincipalDetails) authentication.getPrincipal();

        Long user_id = userEntity.getUser().getId();

        String accessToken = jwtTokenProvider.createAccessToken(user_id);
        String refreshToken = jwtTokenProvider.createRefreshToken(user_id);

        authService.registerRefreshToken(user_id, refreshToken);

        Token token = new Token(accessToken, refreshToken);
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
    public BaseResponse<PostAccessRes> postAccess(@RequestBody PostAccessReq postAccessReq) {
        String refreshToken = postAccessReq.getRefreshToken();
        try {
            AssertRefreshTokenEqualAndValid(refreshToken);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

        try {
            Token newTokens = authService.createAccess(refreshToken);
            PostAccessRes postAccessRes = new PostAccessRes(newTokens);
            return new BaseResponse<>(postAccessRes);
        } catch (BaseException exception) {
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
    public BaseResponse<String> postUser(@RequestBody PostUserReq postUserReq) {
        try {
            String encodedPassword = passwordEncoder.encode(postUserReq.getPassword());
            User user = new User(postUserReq.getName(), postUserReq.getNickname(), postUserReq.getEmail(), encodedPassword, "ROLE_USER", "none", "none");
            userService.createUser(user, postUserReq.isTermsEnable());
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
    public BaseResponse<BaseResponseStatus> PostSignupOauth2(@RequestBody PostSignupOauth2Req postSignupOauth2Req) {
        try{
            userService.createOauth2User(postSignupOauth2Req);
            return new BaseResponse<>(SUCCESS);
        }catch(BaseException exception){
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
    public BaseResponse<String> checkEmail(@RequestParam(required = true) String email) {
        try {
            if (userProvider.checkEmail(email) == 0) { // 새 user
                return new BaseResponse<>("가능한 이메일입니다.");
            } else { // 이미 있는 유저
                return new BaseResponse<>(BaseResponseStatus.POST_USERS_EXISTS_EMAIL);
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
    public BaseResponse<String> checkEmailExistence(@RequestParam(required = true) String email) {
        try {
            if (userProvider.checkEmail(email) == 1) { // email 확인
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
    public BaseResponse<BaseResponseStatus> patchUserPassword(@RequestBody PatchPasswordReq patchPasswordReq) {
        try {
            userService.changePassword(patchPasswordReq);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    public void AssertRefreshTokenEqualAndValid(String token) throws BaseException {
        try {
            Jwts
                    .parserBuilder().setSigningKey(jwtTokenProvider.getAccessKey()).build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new BaseException(EXPIRED_JWT);
        } catch (Exception e) {
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
        try{
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (Exception e){
            throw new BaseException(USERS_DISACCORD_PASSWORD);
        }
    }
}
