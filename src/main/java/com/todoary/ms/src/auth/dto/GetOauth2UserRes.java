package com.todoary.ms.src.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetOauth2UserRes {
    private String name;
    private String email;
    private String provider;
    private String providerId;
}
