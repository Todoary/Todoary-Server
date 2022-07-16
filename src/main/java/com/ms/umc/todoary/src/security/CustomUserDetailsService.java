package com.ms.umc.todoary.src.security;

import com.ms.umc.todoary.src.base.BaseException;
import com.ms.umc.todoary.src.entity.PrincipalDetails;
import com.ms.umc.todoary.src.user.UserProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserProvider userProvider;

    @Autowired
    public CustomUserDetailsService(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            log.info("Load User By Username: "+email);
            return new PrincipalDetails(userProvider.retrieveUserByEmail(email));
        } catch (BaseException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public UserDetails loadUserByUsername(int userId) {
        try {
            log.info("Load User By userId: "+userId);
            return new PrincipalDetails(userProvider.retrieveUserById(userId));
        } catch (BaseException e) {
            log.info(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
