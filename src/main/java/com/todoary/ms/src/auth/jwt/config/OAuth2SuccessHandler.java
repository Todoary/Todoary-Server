package com.todoary.ms.src.auth.jwt.config;

import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

        String targetUrl;

        String accessToken =  jwtTokenProvider.createAccessToken(oAuth2User.getUser().getId());
        String refreshToken =  jwtTokenProvider.createRefreshToken(oAuth2User.getUser().getId());

        targetUrl = UriComponentsBuilder.fromUriString("/auth/login/success")
                .queryParam("accessToken",accessToken)
                .queryParam("refreshToken",refreshToken)
                .build().toUriString();
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}