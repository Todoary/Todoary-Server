package com.todoary.ms.src.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.todoary.ms.src.domain.Provider.APPLE;

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

    public static ProviderAccount none() {
        return new ProviderAccount(Provider.NONE, "none");
    }

    public static ProviderAccount appleFrom(String providerId) {
        return new ProviderAccount(APPLE, providerId);
    }
}
