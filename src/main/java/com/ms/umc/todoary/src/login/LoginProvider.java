package com.ms.umc.todoary.src.login;

import com.ms.umc.todoary.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginProvider {
    private final LoginDao loginDao;
    private final JwtService jwtService;

    @Autowired
    public LoginProvider(LoginDao loginDao, JwtService jwtService) {
        this.loginDao = loginDao;
        this.jwtService = jwtService;
    }


}
