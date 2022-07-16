package com.ms.umc.todoary.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String provider;
    private String providerId;

    public PostUserReq(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}
