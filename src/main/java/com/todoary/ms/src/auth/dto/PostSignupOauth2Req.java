package com.todoary.ms.src.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostSignupOauth2Req {
    private String name;
    private String email;
    private String provider;
    private String providerId;
    @JsonProperty("isTermsEnable")
    private boolean isTermsEnable;
    private String fcm_token;

    public PostSignupOauth2Req(String name, String email, String provider, String providerId, boolean isTermsEnable) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.isTermsEnable = isTermsEnable;
    }
}
