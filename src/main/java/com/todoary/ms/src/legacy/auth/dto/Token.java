package com.todoary.ms.src.legacy.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Token {
    private final String accessToken;
    private final String refreshToken;

}