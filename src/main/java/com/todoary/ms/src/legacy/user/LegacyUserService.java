package com.todoary.ms.src.legacy.user;

import com.todoary.ms.src.legacy.auth.dto.PostSignupOauth2Req;
import com.todoary.ms.src.legacy.user.dto.PatchPasswordReq;
import com.todoary.ms.src.legacy.user.dto.PatchUserReq;
import com.todoary.ms.src.legacy.user.dto.PatchUserRes;
import com.todoary.ms.src.legacy.user.model.User;
import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.todoary.ms.src.common.response.BaseResponseStatus.*;

@Slf4j
@Service
public class LegacyUserService {

    private final PasswordEncoder passwordEncoder;
    private final LegacyUserProvider legacyUserProvider;
    private final LegacyUserDao legacyUserDao;

    @Autowired
    public LegacyUserService(PasswordEncoder passwordEncoder, LegacyUserProvider legacyUserProvider, LegacyUserDao legacyUserDao) {
        this.passwordEncoder = passwordEncoder;
        this.legacyUserProvider = legacyUserProvider;
        this.legacyUserDao = legacyUserDao;
    }

    public User createUser(User user, boolean isTermsEnable) throws BaseException {
        if (legacyUserProvider.checkEmail(user.getEmail(), user.getProvider()) == 1) {
            throw new BaseException(MEMBERS_DUPLICATE_EMAIL);
        }
        if (legacyUserProvider.checkNickname(user.getNickname()) == 1) {
            throw new BaseException(MEMBERS_DUPLICATE_NICKNAME);
        }
        try {
            return this.legacyUserDao.insertUser(user, isTermsEnable);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void createOauth2User(PostSignupOauth2Req postSignupOauth2Req) throws BaseException {
        // provider가 "google", "apple" 인지
        if (!legacyUserProvider.isProviderCorrect(postSignupOauth2Req.getProvider())) {
            throw new BaseException(BaseResponseStatus.INVALID_PROVIDER);
        }
        String nickname = generateRandomNickname();
        while (legacyUserProvider.checkNickname(nickname) == 1) {
            nickname = generateRandomNickname();
        }
        String password = passwordEncoder.encode(postSignupOauth2Req.getProviderId());
        User user = new User(postSignupOauth2Req.getName(), nickname, postSignupOauth2Req.getEmail(),
                password, "ROLE_USER", postSignupOauth2Req.getProvider(), postSignupOauth2Req.getProviderId());
        createUser(user, postSignupOauth2Req.isTermsEnable());
    }

    public Long createAppleUser(PostSignupOauth2Req postSignupOauth2Req) throws BaseException {
        // provider가 "google", "apple" 인지
        if (!legacyUserProvider.isProviderCorrect(postSignupOauth2Req.getProvider())) {
            throw new BaseException(BaseResponseStatus.INVALID_PROVIDER);
        }
        String nickname = generateRandomNickname();
        while (legacyUserProvider.checkNickname(nickname) == 1) {
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
            return new PostProfileImgRes(user_id, this.legacyUserDao.updateProfileImg(user_id, profile_img_url));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyProfileImgToDefault(Long user_id) throws BaseException {
        try {
            this.legacyUserDao.updateProfileImgToDefault(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public PatchUserRes modifyProfile(Long user_id, PatchUserReq patchUserReq) throws BaseException {
        if (legacyUserProvider.checkOtherUserNickname(user_id,patchUserReq.getNickname()) == 1)
            throw new BaseException(MEMBERS_DUPLICATE_NICKNAME);
        try {
            return legacyUserDao.updateProfile(user_id, patchUserReq);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeUser(Long user_id) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            legacyUserDao.updateUserStatus(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeAppleUser(String email) throws BaseException {
        if (legacyUserProvider.checkEmail(email,"apple") == 0)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);
        try {
            legacyUserDao.deleteAppleUser(email);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyAlarm(Long user_id, String alarm, boolean isChecked) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            legacyUserDao.updateAlarm(user_id, alarm, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void modifyFcmToken(Long user_id, String fcm_token) throws BaseException {
        Long fcmTokenId = legacyUserProvider.checkFcmTokenExist(fcm_token);

        if (fcmTokenId != 0) {
            legacyUserDao.updateFcmTokenNull(fcmTokenId);
        }
        try {
            legacyUserDao.updateFcmToken(user_id, fcm_token);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(MODIFY_FAIL_FCMTOKEN);
        }
    }

    public void serviceTerms(Long user_id, String terms, boolean isChecked) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            legacyUserDao.termsStatus(user_id, terms, isChecked);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void changePassword(PatchPasswordReq patchPasswordReq) throws BaseException {
        String email = patchPasswordReq.getEmail();
        //validation
        if (legacyUserProvider.checkEmail(email) != 1)
            throw new BaseException(USERS_EMPTY_USER_EMAIL);

        String encodedPassword = passwordEncoder.encode(patchPasswordReq.getNewPassword());

        try {
            legacyUserDao.updatePassword(email, encodedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public void removeRefreshToken(Long user_id) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            legacyUserDao.deleteRefreshToken(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeFCMToken(Long user_id) throws BaseException {
        if (legacyUserProvider.checkId(user_id) == 0)
            throw new BaseException(USERS_EMPTY_USER_ID);
        try {
            legacyUserDao.deleteFCMToken(user_id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void removeUserExpired(String targetDate) throws BaseException {
        try {
            legacyUserDao.deleteByUserStatus(targetDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
