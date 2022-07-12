package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.config.BaseResponse;
import com.ms.umc.todoary.jwt.TokenProvider;
import com.ms.umc.todoary.src.login.model.PostLoginReq;

import com.ms.umc.todoary.src.login.model.PostLoginRes;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

import static com.ms.umc.todoary.config.BaseResponseStatus.*;
import static com.ms.umc.todoary.utils.ValidationRegex.isRegexEmail;

@RestController
@RequestMapping("/login")
public class LoginController {
    private final LoginService loginService;
    private final LoginProvider loginProvider;
    private final JwtService jwtService;

    @Autowired
    public LoginController(LoginService loginService, LoginProvider loginProvider, JwtService jwtService) {

        this.loginService = loginService;
        this.loginProvider = loginProvider;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @PostMapping("/normal")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            if (postLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (postLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }

            if (!isRegexEmail(postLoginReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            PostLoginRes postLoginRes = loginProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }

    }
}
