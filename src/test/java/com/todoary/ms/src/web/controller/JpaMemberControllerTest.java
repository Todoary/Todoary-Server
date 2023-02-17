package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.web.dto.MemberProfileImgUrlResponse;
import com.todoary.ms.src.web.dto.SigninResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.todoary.ms.src.web.controller.TestUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JpaMemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    static String accessToken;

    @BeforeAll
    void getAccessToken() throws Exception {
        joinMember();

        accessToken = normalLogin().getAccessToken();
        System.out.println("accessToken : " + accessToken);
    }

    @Test
    void 기본_프로필일때_프로필_사진_수정_테스트() throws Exception {
        //given
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
                builder.file(image)
                        .header("Authorization", accessToken))
                .andExpect(status().isOk())
                .andDo(log())
                .andReturn();

        //then
        MemberProfileImgUrlResponse response = getResponseObject(result, MemberProfileImgUrlResponse.class);
        assertThat(response.getProfileImgUrl()).startsWith("https://todoarybucket.s3.ap-northeast-2.amazonaws.com/todoary/users/info/" + response.getMemberId());
    }

    void joinMember() throws Exception {
        String normalJoinRequestBody =
                "{" +
                        "\"name\" : \"memberA\"," +
                        "\"nickname\" : \"nicknameA\"," +
                        "\"email\" : \"emailA\"," +
                        "\"password\" : \"passwordA\"," +
                        "\"isTermsEnable\" : true" +
                        "}";
        mockMvc.perform(
                        post("/auth/jpa/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(normalJoinRequestBody))
                .andExpect(status().isOk());
    }

    SigninResponse normalLogin() throws Exception {
        String loginRequestBody =
                "{" +
                        "\"email\" : \"emailA\"," +
                        "\"password\" : \"passwordA\"" +
                        "}";

        MvcResult result = mockMvc.perform(
                        post("/auth/jpa/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.accessToken").exists())
                .andExpect(jsonPath("$.result.refreshToken").value(""))
                .andReturn();

        SigninResponse signinResponse = getResponseObject(result, SigninResponse.class);
        return signinResponse;
    }

    String createTestImage(String fileName, String contentType, String directory) throws IOException {
        File testImage = File.createTempFile(fileName, "." + contentType, new File(directory));
        testImage.deleteOnExit();
        return testImage.getPath();
    }
}