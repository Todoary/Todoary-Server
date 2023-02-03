package com.todoary.ms.src.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todoary.ms.src.auth.model.Token;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonIgnoreProperties({"newUser"})
public class GoogleSigninResponse {
    @JsonProperty("isNewUser")
    private boolean isNewUser;
    private Token token;
}