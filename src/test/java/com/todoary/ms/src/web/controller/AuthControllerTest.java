package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.service.RefreshTokenService;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Test
    public void refreshToken_재발급_테스트() throws Exception {
        Member member = createMember();

        String refreshTokenCode = jwtTokenProvider.createRefreshToken(member.getId());
        refreshTokenService.save(new RefreshToken(member, refreshTokenCode));

        String requestBody = "{\"refreshToken\" : \""+refreshTokenCode+"\"}";
        mockMvc.perform(
                        post("/auth/jpa/jwt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.accessToken").exists())
                .andExpect(jsonPath("$.result.refreshToken").exists())
                .andDo(print());
    }
    
    @Test
    public void 일반회원가입_테스트() throws Exception {
        String normalJoinRequestBody =
                "{" +
                "\"name\" : \"memberA\"," +
                "\"nickname\" : \"nicknameA\"," +
                "\"email\" : \"emailA\"," +
                "\"password\" : \"passwordA\"," +
                "\"isTermsEnable\" : true" +
                "}";
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/auth/jpa/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(normalJoinRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andDo(print());
    }

    @Test
    public void 일반로그인_테스트() throws Exception {
        일반회원가입_테스트();
        String loginRequestBody =
                "{" +
                "\"email\" : \"emailA\"," +
                "\"password\" : \"passwordA\"" +
                "}";

        mockMvc.perform(
                        post("/auth/jpa/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.accessToken").exists())
                .andExpect(jsonPath("$.result.refreshToken").value(""))
                .andDo(print());
    }

    @Test
    public void 자동로그인_테스트() throws Exception {
        일반회원가입_테스트();
        String autoLoginRequestBody =
                "{" +
                        "\"email\" : \"emailA\"," +
                        "\"password\" : \"passwordA\"" +
                        "}";

        mockMvc.perform(
                        post("/auth/jpa/signin/auto")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(autoLoginRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.accessToken").exists())
                .andExpect(jsonPath("$.result.refreshToken").exists())
                .andDo(print());
    }

    @Test
    public void 이메일_중복체크_테스트_존재O() throws Exception {
        일반회원가입_테스트();

        mockMvc.perform(
                        get("/auth/jpa/email/duplication")
                                .queryParam("email", "emailA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("2017"))
                .andDo(print());
    }

    @Test
    public void 이메일_중복체크_테스트_존재X() throws Exception {
        일반회원가입_테스트();

        mockMvc.perform(
                        get("/auth/jpa/email/duplication")
                                .queryParam("email", "newEmail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("가능한 이메일입니다."))
                .andDo(print());
    }
    
    @Test
    public void 비밀번호_변경_테스트() throws Exception {
        일반회원가입_테스트();

        String email = "emailA";
        String newPassword = "passwordB";

        String changePasswordRequestBody =
                "{" +
                        "\"email\" : \"" + email + "\"," +
                        "\"newPassword\" : \"" + newPassword + "\"" +
                        "}";

        mockMvc.perform(patch("/auth/jpa/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordRequestBody))
                .andExpect(jsonPath("$.code").value("1000"))
                .andDo(print());
    }

    @Test
    public void 비밀변경_후에_로그인_테스트() throws Exception {
        비밀번호_변경_테스트();

        String loginRequestBody =
                "{" +
                        "\"email\" : \"emailA\"," +
                        "\"password\" : \"passwordB\"" +
                        "}";

        mockMvc.perform(
                        post("/auth/jpa/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginRequestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.accessToken").exists())
                .andExpect(jsonPath("$.result.refreshToken").value(""))
                .andDo(print());
    }

    Member createMember() {
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        return memberService.findById(memberService.join(memberJoinParam));
    }

    MemberJoinParam createMemberJoinParam() {
        return new MemberJoinParam("memberA",
                "nicknameA",
                "emailA",
                "passwordA",
                "ROLE_USER",
                true);
    }
}