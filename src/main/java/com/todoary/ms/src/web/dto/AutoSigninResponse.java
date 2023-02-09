package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class AutoSigninResponse {
    private String accessToken;
    private String refreshToken;
}
