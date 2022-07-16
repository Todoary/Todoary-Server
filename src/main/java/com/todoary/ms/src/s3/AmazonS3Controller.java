package com.todoary.ms.src.s3;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.s3.dto.PostProfileImgRes;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/s3")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public AmazonS3Controller(AwsS3Service awsS3Service, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.awsS3Service = awsS3Service;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/images")
    public BaseResponse<PostProfileImgRes> uploadProfileImg(@RequestParam("profile-img") MultipartFile multipartFile, HttpServletRequest request) throws IOException {

        try {
            Long user_id = Long.parseLong(request.getAttribute("user_id").toString());
            String dirName = "todoary/users/info/" + user_id + "/profile-img";
            String profile_img_url = awsS3Service.upload(multipartFile, dirName);
            return new BaseResponse<>(userService.setProfileImg(user_id, profile_img_url));
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }
    }

    @DeleteMapping("/images")
    public BaseResponse<String> deleteImage(@RequestParam("file") String fileName) {
        int result = awsS3Service.deleteS3(fileName);

        if (result == 0) {
            return new BaseResponse("삭제 실패");
        } else {
            return new BaseResponse("삭제 성공");
        }
    }
}
