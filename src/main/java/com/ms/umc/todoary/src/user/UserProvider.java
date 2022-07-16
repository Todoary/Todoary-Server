package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.*;

@Slf4j
@Service
public class UserProvider {

    private final UserDao userDao;

    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public int checkEmail(String email) throws BaseException {
        try {
            return userDao.checkEmail(email);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkName(String name) throws BaseException {
        try {
            return userDao.checkName(name);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkId(int id) throws BaseException {
        try {
            return userDao.checkId(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public User retrieveUserByEmail(String email) throws BaseException {
        if (checkEmail(email) != 1) throw new BaseException(USERS_EMPTY_USER_EMAIL);
        try {
            return userDao.selectUserByEmail(email);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public User retrieveUserById(int id) throws BaseException {
        if (checkId(id) != 1) throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            return userDao.selectUserById(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
