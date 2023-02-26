package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleSigninResponse {
    @JsonProperty("isNewUser")
    private boolean isNewUser;
    private String name;
    private String email;
    private String provider;
    private String providerId;
    private Token token;
    private String appleRefreshToken;
}
