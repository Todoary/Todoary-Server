package com.todoary.ms.src.auth.jwt.filter;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.util.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.todoary.ms.util.BaseResponseStatus.EXPIRED_JWT;
import static com.todoary.ms.util.BaseResponseStatus.INVALID_JWT;
import static com.todoary.ms.util.Secret.JWT_ACCESS_SECRET_KEY;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null) {
            chain.doFilter(request,response);
            return;
        }
        try {
            Jwts
                    .parser()
                    .setSigningKey(JWT_ACCESS_SECRET_KEY)
                    .parseClaimsJws(jwtHeader);

            chain.doFilter(request,response);
        } catch (ExpiredJwtException e) {

            BaseResponse baseResponse = new BaseResponse(EXPIRED_JWT);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), baseResponse);

        } catch (Exception e) {

            BaseResponse baseResponse = new BaseResponse(INVALID_JWT);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), baseResponse);
        }

    }
}
