package com.ms.umc.todoary.src.auth;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.base.BaseResponse;
import com.ms.umc.todoary.src.auth.model.*;

import com.ms.umc.todoary.src.base.BaseResponseStatus;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import static com.ms.umc.todoary.utils.ValidationRegex.isRegexEmail;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public AuthController(AuthService authService, JwtService jwtService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @ResponseBody
    @PostMapping("/signup")
    public BaseResponse<PostUserRes> postUser(@RequestBody PostUserReq postUserReq) {
        // 1. 이메일 검증
        // 1-1. 이메일 입력 안했을 때
        if (postUserReq.getEmail() == null || postUserReq.getEmail().length() == 0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_EMAIL);
        }
        // 1-2. 이메일 형식 검증 (정규식)
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_INVALID_EMAIL);
        }
        // 2. 패스워드 검증
        // 2-1. 패스워드 입력 안했을 때
        if (postUserReq.getPassword() == null || postUserReq.getPassword().length() == 0) {
            return new BaseResponse<>(BaseResponseStatus.POST_USERS_EMPTY_PASSWORD);
        }

        try {
            PostUserRes postUserRes = authService.signUpAndCreateToken(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
