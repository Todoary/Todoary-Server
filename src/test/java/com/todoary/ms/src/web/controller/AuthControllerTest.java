package com.todoary.ms.src.web.controller;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.AuthenticationToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.service.RefreshTokenService;
import com.todoary.ms.src.web.dto.AuthenticationTokenIssueResponse;
import com.todoary.ms.util.BaseResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JpaAuthController authController;

    @Autowired
    private RefreshTokenService refreshTokenService;


    @Test
    public void refreshToken_재발급_테스트() throws Exception {
        Member member = Member.builder().build();
        memberService.join(member);

        String refreshTokenCode = jwtTokenProvider.createRefreshToken(member.getId());
        refreshTokenService.save(new RefreshToken(member, refreshTokenCode));

        String requestBody = "{\"refreshToken\" : \""+refreshTokenCode+"\"}";
        mockMvc.perform(
                        post("/auth/jpa/jwt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.authenticationToken.accessToken").exists())
                .andExpect(jsonPath("$.result.authenticationToken.refreshToken").exists())
                .andDo(print());
    }
}