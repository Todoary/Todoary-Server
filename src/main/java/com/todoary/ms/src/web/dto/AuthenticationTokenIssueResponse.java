package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationTokenIssueResponse {
    private String accessToken;
    private String refreshToken;
}
