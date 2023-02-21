package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.domain.ProviderAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppleSigninResponse {
    @JsonProperty("isNewUser")
    private boolean isNewUser;
    private String name;
    private String email;
    private ProviderAccount providerAccount;
    private String accessToken;
    private String refreshToken;
    private String appleRefreshToken;
}
