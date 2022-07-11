package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.utils.JwtService;
import org.springframework.stereotype.Service;

@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }
}
