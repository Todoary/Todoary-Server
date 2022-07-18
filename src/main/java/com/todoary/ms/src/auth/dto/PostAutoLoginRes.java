package com.todoary.ms.src.auth.dto;

import com.todoary.ms.src.auth.model.Token;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAutoLoginRes {
    private Token token;
}
