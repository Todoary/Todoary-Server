package com.todoary.ms.src.config.auth;

import com.todoary.ms.src.exception.common.TodoaryException;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

import static com.todoary.ms.util.BaseResponseStatus.EMPTY_USER;
import static com.todoary.ms.util.BaseResponseStatus.INVALID_JWT;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginMemberAnnotation = parameter.getParameterAnnotation(LoginMember.class) != null;
        boolean isMemberIdClass = Long.class.equals(parameter.getParameterType());
        return isLoginMemberAnnotation && isMemberIdClass;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String memberName = Optional.ofNullable(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow(() -> new TodoaryException(EMPTY_USER));
        return getMemberIdOrElseThrow(memberName);
    }

    private Long getMemberIdOrElseThrow(String memberName) {
        try {
            return Long.parseLong(memberName);
        } catch (NumberFormatException e) {
            throw new TodoaryException(INVALID_JWT);
        }
    }
}
