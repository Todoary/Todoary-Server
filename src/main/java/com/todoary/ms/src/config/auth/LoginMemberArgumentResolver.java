package com.todoary.ms.src.config.auth;

import com.todoary.ms.src.exception.common.TodoaryException;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static com.todoary.ms.util.BaseResponseStatus.INVALID_AUTH;

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
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String memberId = Optional.ofNullable(request.getAttribute("user_id"))
                .orElseThrow(() -> new TodoaryException(INVALID_AUTH))
                .toString();
        return Long.parseLong(memberId);
    }
}
