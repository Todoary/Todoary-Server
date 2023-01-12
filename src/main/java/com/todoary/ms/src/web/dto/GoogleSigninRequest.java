package com.todoary.ms.src.web.dto;

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
