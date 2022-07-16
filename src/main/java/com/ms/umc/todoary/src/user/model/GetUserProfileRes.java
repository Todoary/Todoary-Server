package com.ms.umc.todoary.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUserProfileRes {
    private int userIdx;
    private String nickName;
    private String introduce;
    private String email;
}
