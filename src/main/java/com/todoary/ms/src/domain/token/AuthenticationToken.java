package com.todoary.ms.src.domain.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationToken {
    private AccessToken accessToken;
    private RefreshToken refreshToken;
}
