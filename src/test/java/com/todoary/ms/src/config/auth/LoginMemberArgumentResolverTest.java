package com.todoary.ms.src.config.auth;

import com.todoary.ms.src.common.auth.annotation.LoginMember;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.todoary.ms.src.common.response.BaseResponseStatus.INVALID_JWT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginMemberArgumentResolverTest.TestController.class)
// @ContextConfiguration(classes = {LoginMemberArgumentResolverTest.TestController.class, WebConfig.class, LoginMemberArgumentResolver.class})
@ComponentScan
class LoginMemberArgumentResolverTest {

    @Autowired
    private MockMvc mvc;

    @RestController
    static class TestController {
        @GetMapping("/test/login-member")
        public Long resolveMember(@LoginMember Long memberId) {
            return memberId;
        }
    }

    @Test
    @WithMockUser(username="1")
    void security에_user_id_가_있을_경우_어노테이션에_주입O() throws Exception {
        // given
        Long expectId = 1L;
        // when
        mvc.perform(get("/test/login-member"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectId.toString()));
    }

    @Test
    @WithMockUser(username="asdf12324")
    void security에_user_id_가_잘못됐을_경우_어노테이션에_주입X() throws Exception {
        // when
        mvc.perform(get("/test/login-member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(INVALID_JWT.isSuccess()))
                .andExpect(jsonPath("$.code").value(INVALID_JWT.getCode()))
                .andExpect(jsonPath("$.message").value(INVALID_JWT.getMessage()));
    }
}