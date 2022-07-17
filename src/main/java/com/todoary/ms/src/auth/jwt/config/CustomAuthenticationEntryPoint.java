package com.todoary.ms.src.auth.jwt.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.todoary.ms.util.BaseResponseStatus.*;

/**
 * 유효하지 않은 JWT, 혹은 유저, 401 에러
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("401 ERROR");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{" + "\"isSuccess\":false, "
                + "\"code\":\"" + INVALID_AUTH.getCode() +"\","
                + "\"message\":\"" + INVALID_AUTH.getMessage() + "\"}");

        response.getWriter().flush();
    }
}
