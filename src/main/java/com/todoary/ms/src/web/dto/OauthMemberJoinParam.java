package com.todoary.ms.src.web.dto;

import com.todoary.ms.src.domain.ProviderAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OauthMemberJoinParam {
    private String name;
    private String email;
    private ProviderAccount providerAccount;
    private String role;
    private boolean isTermsEnable;
}
