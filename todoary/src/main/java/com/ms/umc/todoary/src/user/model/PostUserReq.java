package com.ms.umc.todoary.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostUserReq {
    private String name;
    private String nickName;
    private String email;
    private String password;
    private String check_password;
}
