package com.todoary.ms.src.config.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;

public class WithTodoaryMockUserSecurityContextFactory implements WithSecurityContextFactory<WithTodoaryMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithTodoaryMockUser todoaryUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(todoaryUser.username(), "", Arrays.asList(new SimpleGrantedAuthority(todoaryUser.role())));
        context.setAuthentication(authentication);
        return context;
    }
}
