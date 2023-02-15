package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberJoinParam {
    private String name;
    private String nickname;
    private String email;
    private String password;
    private String role;
    private boolean isTermsEnable;
}
