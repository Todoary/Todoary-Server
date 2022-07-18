package com.todoary.ms.src.auth;

import com.todoary.ms.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class AuthProvider {
    private final AuthDao authDao;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public AuthProvider(AuthDao authDao, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.authDao = authDao;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public boolean isRefreshTokenEqual(String token) {
        if (!authDao.checkRefreshToken(token))
            return false;

        return true;
    }

}
