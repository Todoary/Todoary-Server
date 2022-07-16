package com.todoary.ms.src.user;

import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.DATABASE_ERROR;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User createUser(User user) {
        return this.userDao.insertUser(user);
    }

    public PostProfileImgRes setProfileImg(Long user_id, String profile_img_url) throws BaseException {
        try {
            return new PostProfileImgRes(user_id, this.userDao.updateProfileImg(user_id, profile_img_url));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
