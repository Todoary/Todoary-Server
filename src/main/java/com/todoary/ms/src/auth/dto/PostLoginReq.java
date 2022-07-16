package com.todoary.ms.src.auth.dto;

import lombok.Data;

@Data
public class PostLoginReq {
    private String email;
    private String password;
}
