package com.todoary.ms.src.user;

import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Slf4j
@Service
public class UserProvider {

    private final UserDao userDao;

    @Autowired
    public UserProvider(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 기본적으로 email로 유저를 확인할 때는 항상 provider와 같이 확인해야 한다.
     * email을 이용하여 확인할 때 provider 파라미터가 주어지지 않는다면
     * 일반 회원가입한 유저(provider=="none")를 가져오게 된다.
     * @param email
     * @return User
     * @throws BaseException
     */
    public User retrieveByEmail(String email) throws BaseException{
        return retrieveByEmail(email, "none");
    }
    public User retrieveByEmail(String email, String provider) throws BaseException {
        if (checkEmail(email, provider) == 0)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);
        try {
            return userDao.selectByEmail(email, provider);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public User retrieveById(Long user_id) throws BaseException {
        if (checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            return userDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 기본적으로 email로 유저를 확인할 때는 항상 provider와 같이 확인해야 한다.
     * email을 이용하여 확인할 때 provider 파라미터가 주어지지 않는다면
     * 일반 회원가입한 유저(provider=="none")가 있는 지 체크하게 된다.
     * @param email
     * @return int 0 or 1
     * @throws BaseException
     */
    public int checkEmail(String email) throws BaseException {
        return checkEmail(email, "none");
    }
    public int checkEmail(String email, String provider) throws BaseException {
        try {
            return userDao.checkEmail(email, provider);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkNickname(String nickname) throws BaseException {
        try {
            return userDao.checkNickname(nickname);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkId(Long id) throws BaseException {
        try {
            return userDao.checkId(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRefreshToken(Long id) throws BaseException {
        try {
            return userDao.checkRefreshToken(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
