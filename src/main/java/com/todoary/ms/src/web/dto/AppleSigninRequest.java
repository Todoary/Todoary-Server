package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleSigninRequest {
    private String code;
    private String idToken;
    private String name;
    private String email;
    @JsonProperty("isTermsEnable")
    private boolean isTermsEnable;
}
