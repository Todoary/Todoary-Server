package com.todoary.ms.src.s3;

import com.todoary.ms.src.common.exception.TodoaryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.todoary.ms.src.common.response.BaseResponseStatus.AWS_FILE_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AwsS3ServiceTest {

    @Autowired
    private AwsS3Service awsS3Service;

    @Value("${profile-image.default-url}")
    private String defaultProfileImageUrl;

    @Value("${profile-image.filename-pattern}")
    private String fileNamePatternExp;

    private Pattern fileNamePattern;

    @BeforeEach
    void setup() {
        fileNamePattern = Pattern.compile(fileNamePatternExp);
    }

    @Test
    void 기본_프로필_이미지는_삭제X() {
        // when
        boolean deleted = awsS3Service.fileDelete(defaultProfileImageUrl);
        // then
        assertThat(deleted).isFalse();
    }

    @Test
    void url_유효하지_않을때_파일_삭제X() {
        // given
        String fileUrl = "12345";
        // when
        assertThatThrownBy(() -> awsS3Service.fileDelete(fileUrl))
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(AWS_FILE_NOT_FOUND));
    }

    @Test
    void 없는_파일일때_파일_삭제X() {
        //given
        Matcher matcher = fileNamePattern.matcher(defaultProfileImageUrl);
        assertThat(matcher.matches()).isTrue();

        String fileUrlPrefix = matcher.group(1);
        String fileUrl = fileUrlPrefix + "12345";
        System.out.println("fileUrl = " + fileUrl);
        // when
        assertThatThrownBy(() -> awsS3Service.fileDelete(fileUrl))
                .isInstanceOf(TodoaryException.class)
                .matches(e -> ((TodoaryException) e).getStatus().equals(AWS_FILE_NOT_FOUND));
    }

    @Test
    void 기본_프로필이름_파싱() {
        // given
        Matcher matcher = fileNamePattern.matcher(defaultProfileImageUrl);
        assertThat(matcher.matches()).isTrue();
        System.out.println("matcher = " + matcher);
        System.out.println("matcher.group(0) = " + matcher.group(0));
        System.out.println("matcher.group(1) = " + matcher.group(1));
        System.out.println("matcher.group(2) = " + matcher.group(2));
        // when
        String fileName = awsS3Service.getFileName(defaultProfileImageUrl);
        System.out.println("fileName = " + fileName);
        // then
        assertThat(fileName).isEqualTo(matcher.group(2));
    }
}