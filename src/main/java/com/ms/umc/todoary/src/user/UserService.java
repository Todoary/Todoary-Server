package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.auth.model.PostUserReq;
import com.ms.umc.todoary.src.auth.model.PostUserRes;
import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.DATABASE_ERROR;
import static com.ms.umc.todoary.src.base.BaseResponseStatus.POST_USERS_EXISTS_EMAIL;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;

    private final UserProvider userProvider;

    private final JwtService jwtService;


    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    // 얘 어디로 가지...? UserService?
    public int createUser(PostUserReq postUserReq) throws BaseException {
        try{
            return userDao.insertUser(postUserReq);
        }catch(Exception exception){
            log.info(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
