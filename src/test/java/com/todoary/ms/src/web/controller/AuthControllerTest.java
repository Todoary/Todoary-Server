package com.todoary.ms.src.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.common.exception.TodoaryException;
import com.todoary.ms.src.common.response.BaseResponseStatus;
import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.domain.token.AccessToken;
import com.todoary.ms.src.domain.token.RefreshToken;
import com.todoary.ms.src.service.AppleAuthService;
import com.todoary.ms.src.service.JpaAuthService;
import com.todoary.ms.src.service.MemberService;
import com.todoary.ms.src.web.dto.AppleRevokeRequest;
import com.todoary.ms.src.web.dto.AppleSigninRequest;
import com.todoary.ms.src.web.dto.AppleSigninResponse;
import com.todoary.ms.src.web.dto.MemberJoinParam;
import net.minidev.json.JSONObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.todoary.ms.src.common.response.BaseResponseStatus.MEMBERS_DUPLICATE_EMAIL;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @MockBean
    private AppleAuthService appleAuthService;

    @MockBean
    private JpaAuthService authService;

    @Test
    public void refreshToken_재발급_테스트() throws Exception {
        when(authService.issueAccessToken(anyString())).thenReturn(new AccessToken("accessToken"));
        when(authService.issueRefreshToken(anyString())).thenReturn(new RefreshToken(Member.builder().build(), "refreshToken"));

        String requestBody = "{\"refreshToken\" : \"formalRefreshToken\"}";
        mockMvc.perform(
                        post("/auth/jpa/jwt")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.token.accessToken").exists())
                .andExpect(jsonPath("$.result.token.refreshToken").exists())
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
        when(authService.authenticate(any(), any())).thenReturn(1L);
        when(authService.issueAccessToken(anyLong())).thenReturn(new AccessToken("accessToken"));
        when(authService.issueRefreshToken(anyLong())).thenReturn(new RefreshToken(Member.builder().build(), ""));
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
                .andExpect(jsonPath("$.result.token.accessToken").exists())
                .andExpect(jsonPath("$.result.token.refreshToken").value(""))
                .andDo(print());
    }

    @Test
    public void 자동로그인_테스트() throws Exception {
        when(authService.authenticate(any(), any())).thenReturn(1L);
        when(authService.issueAccessToken(anyLong())).thenReturn(new AccessToken("accessToken"));
        when(authService.issueRefreshToken(anyLong())).thenReturn(new RefreshToken(Member.builder().build(), "refreshToken"));
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
                .andExpect(jsonPath("$.result.token.accessToken").exists())
                .andExpect(jsonPath("$.result.token.refreshToken").exists())
                .andDo(print());
    }

    @Test
    public void 이메일_중복체크_테스트_존재O() throws Exception {
        doThrow(new TodoaryException(MEMBERS_DUPLICATE_EMAIL)).when(memberService).checkEmailDuplicationOfGeneral(any());

        mockMvc.perform(
                        get("/auth/jpa/email/duplication")
                                .queryParam("email", "emailA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("2017"))
                .andDo(print());
    }

    @Test
    public void 이메일_중복체크_테스트_존재X() throws Exception {
        mockMvc.perform(
                        get("/auth/jpa/email/duplication")
                                .queryParam("email", "newEmail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("가능한 이메일입니다."))
                .andDo(print());
    }
    
    @Test
    public void 비밀번호_변경_테스트() throws Exception {
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
    public void 애플_회원가입_테스트() throws Exception {
        //given
        AppleSigninRequest appleSigninRequest = new AppleSigninRequest(
                "code",
                "idToken",
                "name",
                "email",
                true
        );

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("id_token", "idToken");
        tokenResponse.put("refresh_token", "appleRefreshToken");

        when(memberService.existsByProviderAccount(any())).thenReturn(false);
        when(memberService.findByProviderAccount(any())).thenReturn(createMemberWithId(1L));
        when(memberService.joinOauthMember(any())).thenReturn(1L);
        when(authService.issueAccessToken(anyLong())).thenReturn(new AccessToken("accessToken"));
        when(authService.issueRefreshToken(anyLong())).thenReturn(new RefreshToken(Member.builder().build(), "refreshToken"));
        when(appleAuthService.getTokenResponseByCode(anyString())).thenReturn(new JSONObject(tokenResponse));
        when(appleAuthService.getProviderIdFrom(anyString())).thenReturn("providerId");


        //when
        mockMvc.perform(
                        post("/auth/jpa/apple/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(appleSigninRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.isNewUser").value(true));
    }

    @Test
    public void 애플_로그인_테스트() throws Exception {
        //given
        AppleSigninRequest appleSigninRequest = new AppleSigninRequest(
                "code",
                "idToken",
                "name",
                "email",
                true
        );

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("id_token", "idToken");
        tokenResponse.put("refresh_token", "appleRefreshToken");

        when(memberService.existsByProviderAccount(any())).thenReturn(true);
        when(memberService.findByProviderAccount(any())).thenReturn(createMemberWithId(1L));
        when(memberService.joinOauthMember(any())).thenReturn(1L);
        when(authService.issueAccessToken(anyLong())).thenReturn(new AccessToken("accessToken"));
        when(authService.issueRefreshToken(anyLong())).thenReturn(new RefreshToken(Member.builder().build(), "refreshToken"));
        when(appleAuthService.getTokenResponseByCode(anyString())).thenReturn(new JSONObject(tokenResponse));
        when(appleAuthService.getProviderIdFrom(anyString())).thenReturn("providerId");


        //when
        mockMvc.perform(
                        post("/auth/jpa/apple/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(appleSigninRequest)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.code").value("1000"))
                .andExpect(jsonPath("$.result.isNewUser").value(false));
    }

    @Test
    public void 애플_회원_탈퇴_테스트() throws Exception {
        //given
        AppleRevokeRequest appleRevokeRequest = new AppleRevokeRequest(
                "code",
                "email"
        );

        Map<String, String> tokenResponse = new HashMap<>();
        tokenResponse.put("refresh_token", "appleRefreshToken");

        when(appleAuthService.getTokenResponseByCode(anyString())).thenReturn(new JSONObject(tokenResponse));
        when(memberService.findByProviderEmail(any(), any())).thenReturn(createMemberWithId(1L));

        //when
        mockMvc.perform(
                        post("/auth/jpa/revoke/apple")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(new ObjectMapper().writeValueAsString(appleRevokeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("1000"))
                .andReturn();
    }
    
    public Member createMemberWithId(Long id) throws NoSuchFieldException, IllegalAccessException {
        Member member = Member.builder().build();
        Field idField = member.getClass().getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(member, id);

        return member;
    }

    Member createMember() {
        MemberJoinParam memberJoinParam = createMemberJoinParam();
        return memberService.findById(memberService.joinGeneralMember(memberJoinParam));
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