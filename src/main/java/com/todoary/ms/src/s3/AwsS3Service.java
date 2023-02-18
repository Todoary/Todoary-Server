package com.todoary.ms.src.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.BaseResponseStatus.AWS_ACCESS_DENIED;
import static com.todoary.ms.util.BaseResponseStatus.AWS_FILE_NOT_FOUND;

@Slf4j
@RequiredArgsConstructor
@Service
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;

    private final String MEMBER_PROFILE_IMG_FILE_PATH_FORMAT = "todoary/users/info/%d/profile-img";

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    public String upload(MultipartFile multipartFile, Long memberId) {
        File uploadFile = convert(multipartFile);
        return upload(uploadFile, memberId);
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, Long memberId) {
        String fileName = String.format(MEMBER_PROFILE_IMG_FILE_PATH_FORMAT, memberId) + "/" + UUID.randomUUID();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // delete file
    public int fileDelete(String fileName) {
        log.info("file name : "+ fileName);

        try {
            boolean isFileExists = amazonS3Client.doesObjectExist(bucket, fileName);
            if (isFileExists == false) {
                throw new TodoaryException(AWS_FILE_NOT_FOUND);
            }

            log.info((fileName).replace(File.separatorChar, '/'));
            amazonS3Client.deleteObject(this.bucket, fileName);

            return 1;
        } catch (AmazonServiceException exception) {
            throw new TodoaryException(AWS_ACCESS_DENIED);
        }
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("File delete success");
            return;
        }
        log.info("File delete fail");
    }

    // 로컬에 파일 업로드 하기
    private File convert(MultipartFile file) {
        File convertFile = new File(System.getProperty("user.dir") + "/" + file.getOriginalFilename());
        try {
            // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            if (convertFile.createNewFile()) {
                // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                FileOutputStream fos = new FileOutputStream(convertFile);
                fos.write(file.getBytes());
                return convertFile;
            }

            // 파일 변환할 수 없으면 예외
            throw new TodoaryException(AWS_FILE_CONVERT_FAIL);
        } catch (IOException exception) {
            throw new TodoaryException(AWS_FILE_CONVERT_FAIL);
        }
    }
}