package com.todoary.ms.src.auth.dto;

import com.todoary.ms.src.auth.model.AppleUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSignupAppleReq {
    private String code;
    private String idToken;
    private AppleUserInfo appleUserInfo;
}
