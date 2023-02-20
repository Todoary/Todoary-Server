package com.todoary.ms.src.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.todoary.ms.src.exception.common.TodoaryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.todoary.ms.util.BaseResponseStatus.AWS_FILE_CONVERT_FAIL;
import static com.todoary.ms.util.BaseResponseStatus.AWS_FILE_NOT_FOUND;

@Slf4j
@Service
public class AwsS3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${profile-image.default-url}")
    private String defaultImageUrl;
    @Value("${profile-image.path}")
    private String MEMBER_PROFILE_IMG_FILE_PATH_FORMAT;
    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름
    private final Pattern fileNamePattern;

    @Autowired
    public AwsS3Service(AmazonS3Client amazonS3Client,
                        @Value("${profile-image.filename-pattern}") String fileNamePattern) {
        this.amazonS3Client = amazonS3Client;
        this.fileNamePattern = Pattern.compile(fileNamePattern);
    }

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
    public boolean fileDelete(String fileUrl) {
        if (fileUrl == null || fileUrl.equals(defaultImageUrl)) {
            return false;
        }
        String fileName = getFileName(fileUrl);
        if (!amazonS3Client.doesObjectExist(bucket, fileName)) {
            throw new TodoaryException(AWS_FILE_NOT_FOUND);
        }
        log.info((fileName).replace(File.separatorChar, '/'));
        amazonS3Client.deleteObject(this.bucket, fileName);
        return true;
    }

    public String getFileName(String fileUrl) {
        Matcher matcher = fileNamePattern.matcher(fileUrl);
        if (!matcher.matches()) {
            throw new TodoaryException(AWS_FILE_NOT_FOUND);
        }
        log.info("fileName: {}", matcher.group(2));
        return matcher.group(2);
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