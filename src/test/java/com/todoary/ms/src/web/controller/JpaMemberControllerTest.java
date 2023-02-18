package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.config.auth.WithTodoaryMockUser;
import com.todoary.ms.src.constant.MemberConstants;
import com.todoary.ms.src.s3.AwsS3Service;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.MemberProfileImgUrlResponse;
import com.todoary.ms.util.BaseResponseStatus;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.todoary.ms.src.web.controller.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class JpaMemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AwsS3Service awsS3Service;

    @MockBean
    MemberService memberService;

    @Test
    @WithTodoaryMockUser
    void 기본_프로필일때_프로필_사진_수정_테스트() throws Exception {
        //given
        when(awsS3Service.upload(any(), anyLong())).thenReturn("mockingImgUrl");
        when(memberService.getProfileImgUrlById(anyLong())).thenReturn(MemberConstants.MEMBER_DEFAULT_PROFILE_IMG_URL);

        String fileName = "testImage"; //파일명
        String contentType = "png"; //파일타입
        String directory = "src/test/resources/"; //파일경로
        String testImageFilePath = createTestImage(fileName, contentType, directory);
        FileInputStream fileInputStream = new FileInputStream(testImageFilePath);

        MockMultipartFile image = new MockMultipartFile(
                "profile-img",
                fileName + "." + contentType,
                contentType,
                fileInputStream
        );

        //when
        MockMultipartHttpServletRequestBuilder builder = multipart("/jpa/member/profile-img");
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        MvcResult result = mockMvc.perform(
                        builder.file(image))
                .andExpect(status().isOk())
                .andDo(log())
                .andReturn();

        //then
        MemberProfileImgUrlResponse response = getResponseObject(result, MemberProfileImgUrlResponse.class);
        assertThat(response.getProfileImgUrl()).isEqualTo("mockingImgUrl");
    }

    @Test
    @WithTodoaryMockUser
    void 수정된_프로필일때_프로필_사진_수정_테스트() throws Exception {
        //given
        when(awsS3Service.upload(any(), anyLong())).thenReturn("mockingImgUrl");
        when(memberService.getProfileImgUrlById(anyLong()))
                .thenReturn(MemberConstants.MEMBER_DEFAULT_PROFILE_IMG_URL + "/modified");

        String fileName = "testImage"; //파일명
        String contentType = "png"; //파일타입
        String directory = "src/test/resources/"; //파일경로
        String testImageFilePath = createTestImage(fileName, contentType, directory);
        FileInputStream fileInputStream = new FileInputStream(testImageFilePath);

        MockMultipartFile image = new MockMultipartFile(
                "profile-img",
                fileName + "." + contentType,
                contentType,
                fileInputStream
        );

        //when
        MockMultipartHttpServletRequestBuilder builder = multipart("/jpa/member/profile-img");
        builder.with(request -> {
            request.setMethod("PATCH");
            return request;
        });

        MvcResult result = mockMvc.perform(
                        builder.file(image))
                .andExpect(status().isOk())
                .andDo(log())
                .andReturn();

        //then
        MemberProfileImgUrlResponse response = getResponseObject(result, MemberProfileImgUrlResponse.class);
        assertThat(response.getProfileImgUrl()).isEqualTo("mockingImgUrl");
    }
}