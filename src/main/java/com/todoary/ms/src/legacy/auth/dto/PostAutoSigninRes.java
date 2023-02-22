package com.todoary.ms.src.legacy.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAutoSigninRes {
    private Token token;
}
