package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OauthMemberJoinRequest {
    private String name;
    private String email;
    private String provider;
    private String providerId;
    @JsonProperty("isTermsEnable")
    private boolean isTermsEnable;
}
