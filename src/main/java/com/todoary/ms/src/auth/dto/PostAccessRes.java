package com.todoary.ms.src.auth.dto;

import com.todoary.ms.src.auth.model.Token;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PostAccessRes {
    private final Token refreshToken;
}
