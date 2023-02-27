package com.todoary.ms.src.legacy.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;

@Service
public class LegacyAuthProvider {
    private final LegacyAuthDao legacyAuthDao;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public LegacyAuthProvider(LegacyAuthDao legacyAuthDao, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.legacyAuthDao = legacyAuthDao;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    public boolean isRefreshTokenEqual(String token) {
        if (!legacyAuthDao.checkRefreshToken(token))
            return false;

        return true;
    }

}
