package com.todoary.ms.src.user;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.user.dto.GetUserRes;
import com.todoary.ms.src.user.dto.PatchPasswordReq;
import com.todoary.ms.src.user.dto.PatchUserReq;
import com.todoary.ms.src.user.dto.PatchUserRes;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final UserProvider userProvider;

    @Autowired
    public UserController(JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, UserService userService, UserProvider userProvider) {

        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userProvider = userProvider;
    }


    /**
     * 2.5 프로필 조회 api
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
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 2.6 유저 삭제 API
     *
     * @param user_id
     * @return
     */
    @PatchMapping("/status")
    public BaseResponse<BaseResponseStatus> patchUserStatus(HttpServletRequest request) {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            userService.removeUser(user_id);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 2.7 로그아웃 API
     *
     * @param user_id
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

            //Date expiration = new Date(jwtTokenProvider.getExpiration(jwtHeader)); // 남은 유효시간
            //userService.signOutUser(jwtHeader, expiration);
            return new BaseResponse<>(BaseResponseStatus.SUCCESS);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

//    @PatchMapping("/profile-img")
//    public BaseResponse<PostProfileImgRes> uploadProfileImg(@RequestParam("profile-img") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
//
//        try {
//            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
//            String dirName = "todoary/users/info/" + user_id + "/profile-img";
//            String profile_img_url = awsS3Service.upload(multipartFile, dirName);
//            return new BaseResponse<>(userService.setProfileImg(user_id, profile_img_url));
//        } catch (BaseException e) {
//            return new BaseResponse(e.getStatus());
//        }
//    }
//
//    @DeleteMapping("/profile-img")

    @PatchMapping("/profile")
    public BaseResponse<PatchUserRes> patchProfile(HttpServletRequest request, @RequestBody PatchUserReq patchUserReq) throws BaseException {
        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            PatchUserRes patchUserRes = userService.modifyProfile(user_id, patchUserReq);
            return new BaseResponse<>(patchUserRes);
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }

    }
}


