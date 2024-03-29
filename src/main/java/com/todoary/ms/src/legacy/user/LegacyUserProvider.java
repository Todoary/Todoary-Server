package com.todoary.ms.src.legacy.user;

import com.todoary.ms.src.legacy.user.dto.GetAlarmEnabledRes;
import com.todoary.ms.src.legacy.user.model.User;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@Slf4j
@Service
public class LegacyUserProvider {

    private final LegacyUserDao legacyUserDao;

    @Autowired
    public LegacyUserProvider(LegacyUserDao legacyUserDao) {
        this.legacyUserDao = legacyUserDao;
    }

    /**
     * 기본적으로 email로 유저를 확인할 때는 항상 provider와 같이 확인해야 한다.
     * email을 이용하여 확인할 때 provider 파라미터가 주어지지 않는다면
     * 일반 회원가입한 유저(provider=="none")를 가져오게 된다.
     *
     * @param email
     * @return User
     * @throws BaseException
     */
    public User retrieveByEmail(String email) throws BaseException {
        return retrieveByEmail(email, "none");
    }

    public User retrieveByEmail(String email, String provider) throws BaseException {
        if (isDeleted(email,provider)) {
            throw new BaseException(USERS_DELETED_USER);
        }
        if (checkEmail(email, provider) == 0)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);
        try {
            return legacyUserDao.selectByEmail(email, provider);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public User retrieveById(Long user_id) throws BaseException {
        if (checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            return legacyUserDao.selectById(user_id);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public User retrieveByAppleUniqueNo(String provider_id) throws BaseException {
        try {
            return legacyUserDao.selectByProviderId(provider_id);
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 기본적으로 email로 유저를 확인할 때는 항상 provider와 같이 확인해야 한다.
     * email을 이용하여 확인할 때 provider 파라미터가 주어지지 않는다면
     * 일반 회원가입한 유저(provider=="none")가 있는 지 체크하게 된다.
     *
     * @param email
     * @return int 0 or 1
     * @throws BaseException
     */
    public int checkEmail(String email) throws BaseException {
        return checkEmail(email, "none");
    }

    public int checkEmail(String email, String provider) throws BaseException {
        try {
            return legacyUserDao.checkEmail(email, provider);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkNickname(String nickname) throws BaseException {
        try {
            return legacyUserDao.checkNickname(nickname);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public int checkOtherUserNickname(Long user_id,String nickname) throws BaseException {
        try {
            return legacyUserDao.checkOtherUserNickname(user_id,nickname);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkId(Long id) throws BaseException {
        try {
            return legacyUserDao.checkId(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkRefreshToken(Long id) throws BaseException {
        try {
            return legacyUserDao.checkRefreshToken(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkFCMToken(Long id) throws BaseException {
        try {
            return legacyUserDao.checkFCMToken(id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkAppleUniqueNo(String provider_id) throws BaseException {
        try {
            return legacyUserDao.checkAppleUniqueNo(provider_id);
        } catch (Exception exception) {
            log.warn(exception.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean isProviderCorrect(String provider) {
        return (provider.equals("google") || provider.equals("apple"));
    }

    public void assertUserValidById(Long userId) throws BaseException {
        if (checkId(userId) == 0)
            throw new BaseException(BaseResponseStatus.USERS_EMPTY_USER_ID);
    }

    public GetAlarmEnabledRes retrieveAlarmEnabled(Long user_id) throws BaseException {
        try {
            return legacyUserDao.selectAlarmEnabledById(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean isDeleted(String email, String provider) throws BaseException {
        try {
            if (legacyUserDao.isDeleted(email, provider) == 0)
                return false;
            else
                return true;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public Long checkFcmTokenExist(String fcm_token) throws BaseException {
        try {
            Long targetId = legacyUserDao.checkFcmTokenExist(fcm_token);
            return targetId;
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
