package com.todoary.ms.src.web.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RestoreRequest {
    @NotNull(message="USERS_EMPTY_USER_EMAIL")
    private String email;
    private String provider;
    private String providerId;
}
