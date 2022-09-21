package com.todoary.ms.src.user;

import com.todoary.ms.src.auth.dto.PostSignupOauth2Req;
import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.user.dto.*;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

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

    public User createUser(User user, boolean isTermsEnable) throws BaseException {
        if (userProvider.checkEmail(user.getEmail(), user.getProvider()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userProvider.checkNickname(user.getNickname()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }
        try {
            return this.userDao.insertUser(user, isTermsEnable);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void createOauth2User(PostSignupOauth2Req postSignupOauth2Req) throws BaseException {
        // provider가 "google", "apple" 인지
        if (!userProvider.isProviderCorrect(postSignupOauth2Req.getProvider())) {
            throw new BaseException(BaseResponseStatus.INVALID_PROVIDER);
        }
        String nickname = generateRandomNickname();
        while (userProvider.checkNickname(nickname) == 1) {
            nickname = generateRandomNickname();
        }
        String password = passwordEncoder.encode(postSignupOauth2Req.getProviderId());
        User user = new User(postSignupOauth2Req.getName(), nickname, postSignupOauth2Req.getEmail(),
                password, "ROLE_USER", postSignupOauth2Req.getProvider(), postSignupOauth2Req.getProviderId());
        createUser(user, postSignupOauth2Req.isTermsEnable());
    }

    public Long createAppleUser(PostSignupOauth2Req postSignupOauth2Req) throws BaseException {
        // provider가 "google", "apple" 인지
        if (!userProvider.isProviderCorrect(postSignupOauth2Req.getProvider())) {
            throw new BaseException(BaseResponseStatus.INVALID_PROVIDER);
        }
        String nickname = generateRandomNickname();
        while (userProvider.checkNickname(nickname) == 1) {
            nickname = generateRandomNickname();
        }
        String password = passwordEncoder.encode(postSignupOauth2Req.getProviderId());
        User user = new User(postSignupOauth2Req.getName(), nickname, postSignupOauth2Req.getEmail(),
                password, "ROLE_USER", postSignupOauth2Req.getProvider(), postSignupOauth2Req.getProviderId());
        return createUser(user, postSignupOauth2Req.isTermsEnable()).getId();
    }

    private String generateRandomNickname() {
        // 아스키 코드 48 ~ 122까지 랜덤 문자
        // 예: qOji6mPStx
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int maxNicknameLength = 10; // 닉네임 길이 최대 10자
        Random random = new Random();
        String nickname = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 아스키코드 숫자 알파벳 중간에 섞여있는 문자들 제거
                .limit(maxNicknameLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return nickname;
    }

    public PostProfileImgRes setProfileImg(Long user_id, String profile_img_url) throws BaseException {
        try {
            return new PostProfileImgRes(user_id, this.userDao.updateProfileImg(user_id, profile_img_url));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyProfileImgToDefault(Long user_id) throws BaseException {
        try {
            this.userDao.updateProfileImgToDefault(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PatchUserRes modifyProfile(Long user_id, PatchUserReq patchUserReq) throws BaseException {
        if (userProvider.checkOtherUserNickname(user_id,patchUserReq.getNickname()) == 1)
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
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

    public void removeAppleUser(String email) throws BaseException {
        if (userProvider.checkEmail(email,"apple") == 0)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);
        try {
            userDao.deleteAppleUser(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAlarm(Long user_id, String alarm, boolean isChecked) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            userDao.updateAlarm(user_id, alarm, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyFcmToken(Long user_id, String fcm_token) throws BaseException {
        try {
            userDao.updateFcmToken(user_id, fcm_token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(MODIFY_FAIL_FCMTOKEN);
        }
    }

    public void serviceTerms(Long user_id, String terms, boolean isChecked) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            userDao.termsStatus(user_id, terms, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void changePassword(PatchPasswordReq patchPasswordReq) throws BaseException {
        String email = patchPasswordReq.getEmail();
        //validation
        if (userProvider.checkEmail(email) != 1)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);

        String encodedPassword = passwordEncoder.encode(patchPasswordReq.getNewPassword());

        try {
            userDao.updatePassword(email, encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void removeRefreshToken(Long user_id) throws BaseException {
        if (userProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            userDao.deleteRefreshToken(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeUserExpired(String targetDate) throws BaseException {
        try {
            userDao.deleteByUserStatus(targetDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
