package com.todoary.ms.src.auth.jwt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoary.ms.src.auth.AuthService;
import com.todoary.ms.src.auth.dto.GetOauth2SuccessRes;
import com.todoary.ms.src.auth.dto.GetOauth2UserRes;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.auth.model.Token;
import com.todoary.ms.util.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        
        PrincipalDetails oAuth2User = (PrincipalDetails) authentication.getPrincipal();

        GetOauth2SuccessRes getOauth2SuccessRes;
        if (oAuth2User.isNewMember()) { // 새로 가입한 유저
            GetOauth2UserRes getOauth2UserRes = new GetOauth2UserRes(oAuth2User.getMember().getName(),
                    oAuth2User.getMember().getEmail(), oAuth2User.getMember().getProviderAccount().getProvider().toString(), oAuth2User.getMember().getProviderAccount().getProviderId());
            getOauth2SuccessRes = new GetOauth2SuccessRes(true, getOauth2UserRes);
        } else { // 기존 유저
            String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getMember().getId());
            String refreshToken = jwtTokenProvider.createRefreshToken(oAuth2User.getMember().getId());
            authService.registerRefreshToken(oAuth2User.getMember().getId(), refreshToken);
            Token token = new Token(accessToken, refreshToken);
            getOauth2SuccessRes = new GetOauth2SuccessRes(false, token);
        }
        String result = objectMapper.writeValueAsString(new BaseResponse<>(getOauth2SuccessRes));
        response.getWriter().write(result);
    }
}