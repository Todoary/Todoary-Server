package com.todoary.ms.src.common.auth.jwt.filter;

import com.todoary.ms.src.legacy.auth.LegacyAuthService;
import com.todoary.ms.src.legacy.auth.dto.PostSigninRes;
import com.todoary.ms.src.common.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.legacy.auth.model.LegacyPrincipalDetails;
import com.todoary.ms.src.legacy.auth.dto.Token;
import com.todoary.ms.src.legacy.user.model.User;
import com.todoary.ms.src.common.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final LegacyAuthService legacyAuthService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, LegacyAuthService legacyAuthService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.legacyAuthService = legacyAuthService;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Authentication authentication = null;
        try {

            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class); //json으로 넘어온 username과 password user에 담아줌.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
                        // 여기서 PrincipalDetailsService의 loadByUsername 실행됨
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // ^ 갔다와서 실행됨
            LegacyPrincipalDetails legacyPrincipalDetails = (LegacyPrincipalDetails) authentication.getPrincipal();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        LegacyPrincipalDetails legacyPrincipalDetails = (LegacyPrincipalDetails) authResult.getPrincipal();

        Long userid = legacyPrincipalDetails.getMember().getId();
        String accessToken = jwtTokenProvider.createAccessToken(userid);
        String refreshToken = jwtTokenProvider.createRefreshToken(userid);

        legacyAuthService.registerRefreshToken(userid, refreshToken);

        Token token = new Token(accessToken, refreshToken);
        PostSigninRes postSigninRes = new PostSigninRes(token);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), new BaseResponse<PostSigninRes>(postSigninRes));

    }
}
