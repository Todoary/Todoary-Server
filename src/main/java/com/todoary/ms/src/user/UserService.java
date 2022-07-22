package com.todoary.ms.src.user;

import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.user.dto.PatchPasswordReq;
import com.todoary.ms.src.user.dto.PatchUserReq;
import com.todoary.ms.src.user.dto.PatchUserRes;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.todoary.ms.util.BaseResponseStatus.*;

@Slf4j
@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserProvider userProvider;
    private final UserDao userDao;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserProvider userProvider, UserDao userDao) {
        this.passwordEncoder = passwordEncoder;
        this.userProvider = userProvider;
        this.userDao = userDao;
    }

    public User createUser(User user) throws BaseException {
        if (userProvider.checkEmail(user.getEmail(), user.getProvider()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userProvider.checkNickname(user.getNickname()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        try {
            return this.userDao.insertUser(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PostProfileImgRes setProfileImg(Long user_id, String profile_img_url) throws BaseException {
        try {
            return new PostProfileImgRes(user_id, this.userDao.updateProfileImg(user_id, profile_img_url));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PatchUserRes modifyProfile(Long user_id, PatchUserReq patchUserReq) throws BaseException {
        try {
            return userDao.updateProfile(user_id, patchUserReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void removeUser(Long user_id) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            userDao.updateUserStatus(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void changePassword(Long user_id, String Password, PatchPasswordReq patchPasswordReq) throws BaseException {

        //validation
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        if (!passwordEncoder.matches(patchPasswordReq.getExPassword(), Password)) {
            throw new BaseException(USERS_DISACCORD_PASSWORD);
        }
        String encodedPassword = passwordEncoder.encode(patchPasswordReq.getNewPassword());

        try {
            userDao.updatePassword(user_id,encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
