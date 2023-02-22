package com.todoary.ms.src.common.auth;

import com.todoary.ms.src.legacy.auth.model.PrincipalDetails;
import com.todoary.ms.src.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new PrincipalDetails(memberService.findByEmailOfGeneralMember(email));
    }
}
