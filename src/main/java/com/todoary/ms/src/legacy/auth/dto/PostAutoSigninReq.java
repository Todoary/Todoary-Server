package com.todoary.ms.src.legacy.auth.dto;

import lombok.Data;

@Data
public class PostAutoSigninReq {
    private String email;
    private String password;
}
