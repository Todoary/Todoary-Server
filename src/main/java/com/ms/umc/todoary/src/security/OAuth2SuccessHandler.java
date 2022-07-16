package com.ms.umc.todoary.src.security;

import com.ms.umc.todoary.src.entity.PrincipalDetails;
import com.ms.umc.todoary.utils.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ms.umc.todoary.src.base.BaseResponseStatus.INVALID_USER_JWT;

@Slf4j
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;

    public OAuth2SuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
        //this.setDefaultTargetUrl("/oauth2/success");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("onAuthenticationSuccess!!! " + request.toString()+" "+response.toString());
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        String token = jwtService.createJwt(principalDetails.getUsername());
        log.info("{}", token);
        response.addHeader("Authorization", token);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"jwt\":\""+token+"\"}");
        response.getWriter().flush();
    }
}
