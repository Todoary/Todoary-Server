package com.ms.umc.todoary.src.security;

import com.ms.umc.todoary.src.auth.AuthService;
import com.ms.umc.todoary.src.auth.model.PostUserReq;
import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.entity.PrincipalDetails;
import com.ms.umc.todoary.src.entity.User;
import com.ms.umc.todoary.src.user.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserProvider userProvider;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomOAuth2UserService(UserProvider userProvider, AuthService authService, PasswordEncoder passwordEncoder) {
        this.userProvider = userProvider;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 서비스 id (구글, 카카오, 네이버)
        // 지금은 google
        String provider = userRequest.getClientRegistration().getRegistrationId();
        // google 의 PK
        String providerId = oAuth2User.getAttribute("sub");

        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
        // google: sub
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes attributes = OAuthAttributes.of(provider, userNameAttributeName, oAuth2User.getAttributes());

        // 없으면 가입
        User user;
        try {
            signUpIfNewUser(attributes);
            user = userProvider.getUserByEmail(attributes.getEmail());
        } catch (BaseException e) {
            throw new RuntimeException(e.getMessage());
        }

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    private void signUpIfNewUser(OAuthAttributes attributes) throws BaseException {
        // 신규 -> 가입 필요
        // TODO: 이메일? name?
        if (userProvider.checkEmail(attributes.getEmail()) != 1) {
            // google_10082..
            String name = attributes.getProviderId() + "_" + attributes.getAttributes().get(attributes.getNameAttributeKey());
            String nickname = attributes.getName();
            String uuid = UUID.randomUUID().toString().substring(0, 6);
            String password = passwordEncoder.encode("패스워드" + uuid);  // 사용자가 입력한 적은 없지만 임의 생성
            String email = attributes.getEmail();
            log.info(name);
            authService.createUser(new PostUserReq(name, nickname, email, password));
        }
    }
}
