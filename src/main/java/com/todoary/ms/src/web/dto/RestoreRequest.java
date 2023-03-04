package com.todoary.ms.src.web.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RestoreRequest {
    @NotNull(message="USERS_EMPTY_USER_EMAIL")
    private String email;
    private String provider;
    private String providerId;
}
