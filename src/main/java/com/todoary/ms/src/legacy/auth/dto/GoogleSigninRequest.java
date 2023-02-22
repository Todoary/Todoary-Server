package com.todoary.ms.src.legacy.auth.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class GoogleSigninRequest {
    public String name;
    public String email;
    public String providerId;
}
