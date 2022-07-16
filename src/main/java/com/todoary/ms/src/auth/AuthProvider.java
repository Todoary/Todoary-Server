package com.todoary.ms.src.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthProvider {
    private final AuthDao authDao;

    @Autowired
    public AuthProvider(AuthDao authDao) {
        this.authDao = authDao;
    }

    public boolean isRefreshTokenEqual(String token) {
        if (!authDao.checkRefreshToken(token))
            return false;

        return true;
    }
}
