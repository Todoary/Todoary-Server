package com.todoary.ms.src.auth.jwt.config;

import com.todoary.ms.util.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static com.todoary.ms.util.BaseResponseStatus.INVALID_AUTH;
import static com.todoary.ms.util.ErrorLogWriter.writeExceptionWithRequest;

/**
 * 유효하지 않은 JWT, 혹은 유저, 401 에러
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        writeExceptionWithRequest(new BaseException(INVALID_AUTH), request, messageBody);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{" + "\"isSuccess\":false, "
                + "\"code\":\"" + INVALID_AUTH.getCode() +"\","
                + "\"message\":\"" + INVALID_AUTH.getMessage() + "\"}");

        response.getWriter().flush();
    }
}
