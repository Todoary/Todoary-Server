package com.todoary.ms.src.legacy.user;

import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.legacy.user.dto.*;
import com.todoary.ms.src.legacy.user.model.User;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.user.dto.*;
import com.todoary.ms.src.legacy.BaseException;
import com.todoary.ms.src.common.response.BaseResponse;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.common.util.ErrorLogWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithMessage;
import static com.todoary.ms.src.common.util.ErrorLogWriter.writeExceptionWithAuthorizedRequest;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserProvider userProvider;
    private final UserDao userDao;
    private final AwsS3Service awsS3Service;
    private String default_profile_img_url;

    @Autowired
    public UserController(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, UserService userService, UserProvider userProvider, UserDao userDao, AwsS3Service awsS3Service) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userProvider = userProvider;
        this.userDao = userDao;
        this.awsS3Service = awsS3Service;
    }

    /**
     * 2.1 닉네임 및 한줄소개 변경 API
     *
     * @param request
     * @return
     */

    @PatchMapping("/profile")
    public BaseResponse<PatchUserRes> patchProfile(HttpServletRequest request, @RequestBody PatchUserReq patchUserReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            PatchUserRes patchUserRes = userService.modifyProfile(user_id, patchUserReq);
            return new BaseResponse<>(patchUserRes);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchUserReq.toString());
            return new BaseResponse(e.getStatus());
        }
    }

    /**
     * 2.2 프로필 사진 수정 API
     *
     * @param request
     * @return
     */
    @PatchMapping("/profile-img")
    public BaseResponse<PostProfileImgRes> uploadProfileImg(@RequestParam("profile-img") MultipartFile multipartFile, HttpServletRequest request) {

        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());

            String profile_img_fileKey = userProvider.retrieveById(user_id).getProfile_img_url();
            if (!profile_img_fileKey.equals("https://todoarybucket.s3.ap-northeast-2.amazonaws.com/todoary/users/admin/default_profile_img.jpg"))
                awsS3Service.fileDelete(profile_img_fileKey.substring(54));

            String dirName = "todoary/users/info/" + user_id + "/profile-img";
            String profile_img_url = awsS3Service.upload(multipartFile, user_id);
            return new BaseResponse<>(userService.setProfileImg(user_id, profile_img_url));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse(e.getStatus());
        }
    }

    /**
     * 2.3 프로필 사진 삭제 API
     *
     * @param request
     * @return
     */

    // ToDo: 유저의 프로필 사진을 삭제한다는 것은 회원 탈퇴를 의미 (프로필 사진 변경 != 삭제) >>> 논의 필요
    @PatchMapping("/profile-img/default")
    public BaseResponse<String> deleteProfileImg(HttpServletRequest request) throws BaseException {
        Long user_id = Long.parseLong(request.getAttribute("user_id").toString());

        int result = 0;
        try {
            String filekey = userProvider.retrieveById(user_id).getProfile_img_url();
            if (filekey.equals("https://todoarybucket.s3.ap-northeast-2.amazonaws.com/todoary/users/admin/default_profile_img.jpg"))
                return new BaseResponse<>("삭제에 성공하였습니다.");

            filekey = userProvider.retrieveById(user_id).getProfile_img_url().substring(54);
            awsS3Service.fileDelete(filekey);
            userService.modifyProfileImgToDefault(user_id);
            return new BaseResponse<>("삭제에 성공하였습니다.");
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 2.4 프로필 조회 api
     *
     * @param request
     * @return profileImgUrl, nickname, introduce, email
     * @throws BaseException
     */
    @GetMapping("")
    public BaseResponse<GetUserRes> getProfile(HttpServletRequest request) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            User user = userProvider.retrieveById(user_id);
            GetUserRes getUserRes = new GetUserRes(user.getProfile_img_url(), user.getNickname(), user.getIntroduce(), user.getEmail());
            return new BaseResponse<>(getUserRes);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 2.5 유저 삭제 API
     *
     * @param request
     * @return
     */
    @PatchMapping("/status")
    public BaseResponse<BaseResponseStatus> patchUserStatus(HttpServletRequest request) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.removeUser(user_id);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.6 로그아웃 API
     *
     * @param request
     * @return
     */

    // Todo : access token 유효시간 가져오기
    @PostMapping("/signout")
    public BaseResponse<BaseResponseStatus> logout(HttpServletRequest request) {
        try {
            String jwtHeader = request.getHeader("Authorization");
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            /* remove refreshToken */
            if (userProvider.checkRefreshToken(user_id) == 1) {
                userService.removeRefreshToken(user_id);
            }

            /* remove FCM_token */
            if (userProvider.checkFCMToken(user_id) == 1) {
                userService.removeFCMToken(user_id);
            }

            //Date expiration = new Date(jwtTokenProvider.getExpiration(jwtHeader)); // 남은 유효시간
            //userService.signOutUser(jwtHeader, expiration);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.7.1 Todoary 알림 활성화 api
     */
    @PatchMapping("/alarm/todo")
    public BaseResponse<BaseResponseStatus> patchTodoAlarmStatus(HttpServletRequest request, @RequestBody
    PatchAlarmReq patchAlarmReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.modifyAlarm(user_id, "alarm_todo", patchAlarmReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchAlarmReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.7.2 하루기록 알림 활성화 api
     */
    @PatchMapping("/alarm/diary")
    public BaseResponse<BaseResponseStatus> patchDiaryAlarmStatus(HttpServletRequest request, @RequestBody PatchAlarmReq patchAlarmReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.modifyAlarm(user_id, "alarm_diary", patchAlarmReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchAlarmReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.7.3 일기 알림 활성화 api
     */
    @PatchMapping("/alarm/remind")
    public BaseResponse<BaseResponseStatus> patchRemindAlarmStatus(HttpServletRequest request, @RequestBody PatchAlarmReq patchAlarmReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.modifyAlarm(user_id, "alarm_remind", patchAlarmReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchAlarmReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }


    private Long getUserIdFromRequest(HttpServletRequest request) throws BaseException {
        Long userId = Long.parseLong(request.getAttribute("user_id").toString());
        userProvider.assertUserValidById(userId);
        return userId;
    }


    /**
     * 2.8 알람 활성화 여부 조회 api
     */
    @GetMapping("/alarm")
    public BaseResponse<GetAlarmEnabledRes> getAlarmEnabled(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            userProvider.assertUserValidById(userId);
            return new BaseResponse<>(userProvider.retrieveAlarmEnabled(userId));
        } catch (BaseException e) {
            ErrorLogWriter.writeExceptionWithAuthorizedRequest(e, request);
            return new BaseResponse<>(e.getStatus());
        }

    }


    /**
     * 2.9 마케팅  동의 api
     */
    @PatchMapping("/service/terms")
    public BaseResponse<BaseResponseStatus> patchTermsStatus(HttpServletRequest request, @RequestBody PatchTermsReq patchTermsReq) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.serviceTerms(user_id, "terms", patchTermsReq.isChecked());
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            writeExceptionWithAuthorizedRequest(e, request, patchTermsReq.toString());
            return new BaseResponse<>(e.getStatus());
        }
    }

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void UserRemove() throws BaseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String targetDate = dateFormat.format(new Date());

        try {
            userService.removeUserExpired(targetDate);
            log.info("보관기간이 만료된 비활성화 계정들을 삭제하는데 성공했습니다.");
        } catch (BaseException e) {
            writeExceptionWithMessage(e, "유저 삭제 실패 | UserController/UserRemove()");
        }
    }

    /**
     * 2.10 FCM 토큰 갱신 api
     */
    @PatchMapping("/fcm_token")
    public BaseResponse FCMTokenRefresh(HttpServletRequest request, @RequestBody PatchFcmTokenReq patchFcmTokenReq) {
        String fcm_token = patchFcmTokenReq.getFcm_token();
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.modifyFcmToken(user_id, fcm_token);
            return new BaseResponse("FCM 토큰 갱신에 성공했습니다.");
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}


