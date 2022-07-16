package com.ms.umc.todoary.utils;

import com.ms.umc.todoary.src.base.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class JwtAuthorizationFilter extends GenericFilterBean {

    private final JwtService jwtService;

    @Autowired
    public JwtAuthorizationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = jwtService.resolveToken(httpServletRequest);
        String requestUri = httpServletRequest.getRequestURI();
        if(StringUtils.hasText(jwt) && jwtService.validateToken(jwt)){
            try {
                Authentication authentication = jwtService.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("인증 완료, uri: "+requestUri+" 인증 정보: "+authentication.getName());
            } catch (BaseException e) {
                throw new RuntimeException(e);
            }
        }else{
            logger.info("토큰이 유효하지 않음, uri: "+requestUri);
        }
        chain.doFilter(request, response);
    }
}
