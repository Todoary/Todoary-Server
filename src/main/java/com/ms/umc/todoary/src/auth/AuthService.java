package com.ms.umc.todoary.src.auth;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.auth.model.*;
import com.ms.umc.todoary.src.entity.User;
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
    private final AuthDao authDao;
    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserProvider userProvider, UserService userService, AuthDao authDao, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.authDao = authDao;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        User user = userProvider.getUserByEmail(postLoginReq.getEmail());
        if (passwordEncoder.matches(postLoginReq.getPassword(), user.getPassword())) {
            String jwt = jwtService.createJwt(postLoginReq.getEmail());
            // if (postLoginReq.isAutoLoginChecked() == true)
            return new PostLoginRes(user.getId(), jwt);
            // else
            //     return new PostLoginRes(id, null);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // 얘 어디로 가지...? UserService?
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인
        if(userProvider.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        try{
            postUserReq.setPassword(passwordEncoder.encode(postUserReq.getPassword()));
            int userIdx = authDao.insertUser(postUserReq);
            String jwt = jwtService.createJwt(postUserReq.getEmail());
            return new PostUserRes(jwt, userIdx);
        }catch(Exception exception){
            log.debug(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
