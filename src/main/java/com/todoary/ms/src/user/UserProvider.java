package com.todoary.ms.src.user;

import com.todoary.ms.src.user.dto.GetUserRes;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public User retrieveByEmail(String email) throws BaseException {
        try {
            return userDao.selectByEmail(email);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public User retrieveById(Long user_id) throws BaseException {
        try {
            return userDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
