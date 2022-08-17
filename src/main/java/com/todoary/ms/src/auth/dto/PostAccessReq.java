package com.todoary.ms.src.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAccessReq {
    private String refreshToken;
    private String fcm_token;
}
