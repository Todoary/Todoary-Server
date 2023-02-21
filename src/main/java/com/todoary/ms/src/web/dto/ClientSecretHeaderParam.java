package com.todoary.ms.src.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ClientSecretHeaderParam {
    private String keyId;
    private String signature;
}
