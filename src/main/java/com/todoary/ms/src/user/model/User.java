package com.todoary.ms.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private String role;
    private String provider;
    private String provider_id;

    public User(String username, String nickname,String email, String password, String role, String provider, String provider_id) {
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.provider_id = provider_id;
    }


}
