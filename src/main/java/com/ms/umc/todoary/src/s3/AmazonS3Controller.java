package com.ms.umc.todoary.src.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
public class AmazonS3Controller {

    private final AwsS3Service awsS3Service;

    @PostMapping("/images")
    public String upload(@RequestParam("profile-img") MultipartFile multipartFile) throws IOException {
        return awsS3Service.upload(multipartFile, "todoary/users/profileImg");
    }

    @DeleteMapping("/images")
    public String deleteImage(@RequestParam("file") String fileName) {
        int result = awsS3Service.deleteS3(fileName);

        if (result == 0) {
            return "삭제 실패";
        } else {
            return "삭제 성공";
        }
    }
}
