package com.todoary.ms.src.legacy.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRevokeAppleReq {
    private String email;
    private String code;
}
