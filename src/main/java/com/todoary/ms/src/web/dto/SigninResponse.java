package com.todoary.ms.src.web.dto;

import com.todoary.ms.src.domain.token.AuthenticationToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SigninResponse {
    private AuthenticationToken authenticationToken;
}
