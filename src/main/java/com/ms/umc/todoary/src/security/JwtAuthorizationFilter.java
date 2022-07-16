package com.ms.umc.todoary.src.security;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.utils.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = jwtService.getJwt(httpServletRequest);
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
        filterChain.doFilter(request, response);
    }
}
