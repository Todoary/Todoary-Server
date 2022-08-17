package com.todoary.ms.src.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostSigninReq {
    private String email;
    private String password;
    private String fcm_token;
}
