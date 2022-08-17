package com.todoary.ms.src.auth.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AppleUserInfo {
    private final String name;
    private final String email;
}