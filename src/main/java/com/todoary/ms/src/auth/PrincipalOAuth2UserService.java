package com.todoary.ms.src.auth;

import com.todoary.ms.src.auth.model.PrincipalDetails;
import com.todoary.ms.src.user.UserProvider;
import com.todoary.ms.src.user.UserService;
import com.todoary.ms.src.user.model.User;
import com.todoary.ms.util.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final UserProvider userProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PrincipalOAuth2UserService(UserProvider userProvider, UserService userService, PasswordEncoder passwordEncoder) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

//    Name: [112863152981684584640],
//    Granted Authorities: [[ROLE_USER, SCOPE_https://www.googleapis.com/auth/userinfo.email, SCOPE_https://www.googleapis.com/auth/userinfo.profile, SCOPE_openid]],
//    User Attributes: [{sub=112863152981684584640, name=유경종, given_name=경종, family_name=유, picture=https://lh3.googleusercontent.com/a/AItbvml_HmcgbKrybHWGtcQJ-E9nnUft8e_XxXbhsNVf=s96-c, email=rudwhd515@gmail.com, email_verified=true, locale=ko}]

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String name = (String) oAuth2User.getAttributes().get("name");
        String email = (String) oAuth2User.getAttributes().get("email");
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String provider_id = (String) oAuth2User.getAttributes().get("sub");

        User user = null;
        try {
            if (userProvider.checkEmail(email, provider) == 1)
                user = userProvider.retrieveByEmail(email, provider);
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
        if (user == null) {
            System.out.println("구글 로그인 최초입니다. 회원가입을 진행합니다.");
            user = new User(name, "", email, "", "", provider, provider_id);
            // 여기서 회원 가입 실제로 하지 않고, 가능 여부 파악 후 클라이언트에서 /auth/signup/oauth2 api 호출하여 실제 회원가입함
            // try {
            //     userService.createUser(user);
            // } catch (BaseException e) {
            //     e.printStackTrace();
            // }
            return new PrincipalDetails(user, oAuth2User.getAttributes(), true);
        } else {
            System.out.println("구글 로그인 기록이 있습니다. 로그인을 진행합니다.");
            return new PrincipalDetails(user, oAuth2User.getAttributes(), false);
        }

    }



}
