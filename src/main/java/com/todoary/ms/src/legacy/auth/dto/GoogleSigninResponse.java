package com.todoary.ms.src.legacy.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@JsonIgnoreProperties({"newUser"})
public class GoogleSigninResponse {
    @JsonProperty("isNewUser")
    private boolean isNewUser;
    private Token token;
}
