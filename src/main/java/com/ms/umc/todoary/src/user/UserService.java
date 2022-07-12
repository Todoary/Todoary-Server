package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.config.BaseException;
import com.ms.umc.todoary.src.user.model.PostUserReq;
import com.ms.umc.todoary.src.user.model.PostUserRes;
import com.ms.umc.todoary.utils.SHA256;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.config.BaseResponseStatus.*;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 이메일 중복 확인
        if(userDao.checkEmail(postUserReq.getEmail()) ==1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try{
            //암호화
            pwd = passwordEncoder.encode(postUserReq.getPassword());;  postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        PostUserRes postUserRes = userDao.insertUser(postUserReq);
        System.out.println(pwd);
        return postUserRes;
    }
}
