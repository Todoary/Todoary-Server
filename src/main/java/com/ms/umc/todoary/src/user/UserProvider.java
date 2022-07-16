package com.ms.umc.todoary.src.user;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.DATABASE_ERROR;

@Slf4j
@Service
public class UserProvider {

    private final UserDao userDao;

    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    public int checkEmail(String email) throws BaseException {
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }


    public User getUserByEmail(String email) throws BaseException {
        try{
            return userDao.findByEmail(email);
        } catch (Exception exception){
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public User getUserById(int id) throws BaseException {
        try{
            return userDao.findById(id);
        } catch (Exception exception){
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
