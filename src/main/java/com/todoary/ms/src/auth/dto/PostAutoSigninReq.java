package com.todoary.ms.src.auth.dto;

import lombok.Data;

@Data
public class PostAutoSigninReq {
    private String email;
    private String password;
    private String fcm_token;
}
