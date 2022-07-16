package com.ms.umc.todoary.src.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.umc.todoary.src.auth.model.PostLoginReq;
import com.ms.umc.todoary.src.entity.PrincipalDetails;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.SUCCESS;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public JwtAuthenticationFilter(JwtService jwtService, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.jwtService = jwtService;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     * /login 요청 시 로그인 시도를 위해 실행되는 함수
     * @param request from which to extract parameters and perform the authentication
     * @param response the response, which may be needed if the implementation has to do a
     * redirect as part of a multi-stage authentication process (such as OpenID).
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper om = new ObjectMapper();
        PostLoginReq postLoginReq = null;
        try {
            postLoginReq = om.readValue(request.getInputStream(), PostLoginReq.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(postLoginReq.getEmail(), postLoginReq.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);

        PrincipalDetails user = (PrincipalDetails) authentication.getPrincipal();
        log.info("Authentication : " + user.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        PrincipalDetails principalDetailis = (PrincipalDetails) authResult.getPrincipal();

        String jwt = jwtService.createJwt(principalDetailis.getUser().getId());

        response.addHeader("Authorization", jwt);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{" + "\"isSuccess\":true, "
                + "\"code\":\"" + SUCCESS.getCode() + "\","
                + "\"message\":\"" + SUCCESS.getMessage() + "\"}");
        response.getWriter().flush();
    }
}
