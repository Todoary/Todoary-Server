package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class AppleSigninResponse {
    private Boolean isNewUser;
    private Boolean isDeactivatedUser;
    private String name;
    private String email;
    private String provider;
    private String providerId;
    private Token token;
    private String appleRefreshToken;
}
