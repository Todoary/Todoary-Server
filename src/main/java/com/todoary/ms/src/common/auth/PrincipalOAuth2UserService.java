package com.todoary.ms.src.common.auth;

import com.todoary.ms.src.domain.Member;
import com.todoary.ms.src.domain.ProviderAccount;
import com.todoary.ms.src.legacy.auth.model.PrincipalDetails;
import com.todoary.ms.src.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberService memberService;



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

        ProviderAccount providerAccount = ProviderAccount.from(provider, provider_id);
        Member member = memberService.findByProvider(email, providerAccount);

        if (member == null) {
            System.out.println("구글 로그인 최초입니다. 회원가입을 진행합니다.");
            member = Member.builder()
                        .name(name)
                        .email(email)
                        .providerAccount(providerAccount)
                        .build();

            return new PrincipalDetails(member, oAuth2User.getAttributes(), true);
        } else {
            System.out.println("구글 로그인 기록이 있습니다. 로그인을 진행합니다.");
            return new PrincipalDetails(member, oAuth2User.getAttributes(), false);
        }
    }



}
