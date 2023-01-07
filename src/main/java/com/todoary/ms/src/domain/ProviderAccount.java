package com.todoary.ms.src.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
@Embeddable
public class ProviderAccount {
    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerId;

    public ProviderAccount(Provider provider, String providerId) {
        this.provider = provider;
        this.providerId = providerId;
    }

    public static ProviderAccount create(Provider provider, String providerId) {
        return new ProviderAccount(provider, providerId);
    }
}
