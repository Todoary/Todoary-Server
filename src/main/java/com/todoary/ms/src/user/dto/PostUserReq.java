package com.todoary.ms.src.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostUserReq {
    private String username;
    private String nickname;
    private String email;
    private String password;
}
