package com.ms.umc.todoary.src.auth;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.auth.model.*;
import com.ms.umc.todoary.src.user.UserProvider;
import com.ms.umc.todoary.src.user.UserService;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.*;

@Slf4j
@Service
public class AuthService {

    private final UserProvider userProvider;

    private final UserService userService;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserProvider userProvider, UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public PostUserRes signUpAndCreateToken(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userProvider.checkNickname(postUserReq.getNickname()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        try {
            postUserReq.setPassword(passwordEncoder.encode(postUserReq.getPassword()));
            int userIdx = userService.createUser(postUserReq); // 유저 생성
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(jwt, userIdx);
        } catch (Exception exception) {
            log.info(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
