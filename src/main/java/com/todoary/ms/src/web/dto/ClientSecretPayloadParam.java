package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.Key;
import java.util.Date;

@Getter
@AllArgsConstructor
public class ClientSecretPayloadParam {
    private String issuer;
    private Date expiration;
    private String audience;
    private String subject;
    private Key privateKey;
}
