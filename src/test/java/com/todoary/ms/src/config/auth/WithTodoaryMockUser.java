package com.todoary.ms.src.config.auth;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithTodoaryMockUserSecurityContextFactory.class)
public @interface WithTodoaryMockUser {
    String username() default "1";
    String role() default "USER";
}
