package com.todoary.ms.src.s3;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/images")
    public String upload(@RequestParam("profile-img") MultipartFile multipartFile, HttpServletRequest request) throws IOException {
        String accessToken = request.getHeader("Authorization");

        Long userid = Long.parseLong(jwtTokenProvider.getUseridFromAcs(accessToken));
        System.out.println(userid);

        String dirName = "todoary/users/" + userid + "/profimeImg";
        return awsS3Service.upload(multipartFile, "todoary/users/profileImg");
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
