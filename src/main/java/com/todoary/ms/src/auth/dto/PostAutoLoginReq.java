package com.todoary.ms.src.auth.dto;

import lombok.Data;

@Data
public class PostAutoLoginReq {
    private String email;
    private String password;
}
