package com.todoary.ms.src.legacy.auth.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PostAccessRes {
    private final Token token;
}
