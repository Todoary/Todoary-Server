package com.ms.umc.todoary.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSignInRes {
    private int id;
    private String jwt;
}
