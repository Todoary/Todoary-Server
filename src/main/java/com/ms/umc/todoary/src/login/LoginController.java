package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.config.BaseResponse;
import com.ms.umc.todoary.src.login.model.PostLoginReq;

import com.ms.umc.todoary.src.login.model.PostLoginRes;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("")
    @ResponseBody
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        PostLoginRes postLoginRes = this.loginService.logIn(postLoginReq);

        return new BaseResponse<>(postLoginRes);
    }
}
