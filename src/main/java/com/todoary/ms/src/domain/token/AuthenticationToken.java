package com.todoary.ms.src.domain.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationToken {
    private String accessToken;
    private String refreshToken;
}
