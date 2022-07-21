package com.todoary.ms.src.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BlackList {
    private final String accessToken;
    private Long expiration;
}

